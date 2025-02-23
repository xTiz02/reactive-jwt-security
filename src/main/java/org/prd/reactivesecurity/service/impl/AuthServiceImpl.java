package org.prd.reactivesecurity.service.impl;

import org.prd.reactivesecurity.persistence.dto.AuthenticationRequest;
import org.prd.reactivesecurity.persistence.dto.AuthenticationResponse;
import org.prd.reactivesecurity.persistence.dto.UserDto;
import org.prd.reactivesecurity.persistence.entity.RefreshToken;
import org.prd.reactivesecurity.persistence.entity.User;
import org.prd.reactivesecurity.persistence.repo.RoleRepository;
import org.prd.reactivesecurity.persistence.repo.UserRepository;
import org.prd.reactivesecurity.service.AuthService;
import org.prd.reactivesecurity.service.BlacklistedService;
import org.prd.reactivesecurity.service.TokenService;
import org.prd.reactivesecurity.util.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final BlacklistedService blacklistedService;
    private final JwtService jwtProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtProvider,
                           RoleRepository roleRepository,
                            TokenService tokenService,
                            BlacklistedService blacklistedService
                           ) {
        this.userRepository = userRepository;
        this.blacklistedService = blacklistedService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.jwtProvider = jwtProvider;
        this.roleRepository = roleRepository;

    }


    @Override
    public Mono<AuthenticationResponse> login(AuthenticationRequest dto) {
        return userRepository.findByUsername(dto.username())
                .filter(user -> passwordEncoder.matches(dto.password(), user.getPassword()))
                .map(user -> {
                    String accessToken = jwtProvider.generateToken(user);
                    return new AuthenticationResponse(
                            accessToken,
                            jwtProvider.generateRefreshToken(user,jwtProvider.getIssuedAt(accessToken)));
                })
                .switchIfEmpty(Mono.error(new Throwable("Bad credentials")));
    }

    @Override
    public Mono<UserDto> register(UserDto userDto) {
        Mono<User> user = userRepository.findByUsername(userDto.username());
        return user.hasElement()
                .flatMap(hasElement -> {
                    if (hasElement) {
                        return Mono.error(new Throwable("user already exists"));
                    }
                    return roleRepository.findByName("USER")
                            .flatMap(role -> {
                                User userToSave = UserMapper.mapToEntity(userDto);
                                userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
                                userToSave.setStatus(true);
                                userToSave.setRole(role); // Se asigna el rol correctamente
                                return userRepository.save(userToSave)
                                        .map(UserMapper::mapToModel)
                                        .switchIfEmpty(Mono.error(new Throwable("Error saving user")));
                            })
                            .switchIfEmpty(Mono.error(new Throwable("Role not found")));
                });
    }

    @Override
    public Mono<AuthenticationResponse> refreshAccessToken(String refreshToken) {
        String jti = jwtProvider.extractJti(refreshToken);
        String username = jwtProvider.getSubject(refreshToken);

        return verificateAndReturnRefreshToken(jti)
                .flatMap(rf -> userRepository.findByUsername(username)
                            .map(user ->{
                                String newAccToken = jwtProvider.generateToken(user);
                                return new AuthenticationResponse(newAccToken, rf.getToken());
                            })
                            .switchIfEmpty(Mono.error(new Throwable("User not found")))
                ).onErrorMap(error -> new Throwable(error.getMessage(), error));
    }

    @Override
    public Mono<AuthenticationResponse> createRefreshToken(String rf) {
        String username = jwtProvider.getSubject(rf);
        String jti = jwtProvider.extractJti(rf);

        return verificateAndReturnRefreshToken(jti)
                .flatMap(oldToken -> userRepository.findByUsername(username)
                        .flatMap(user -> {
                            String newAccessJwt = jwtProvider.generateToken(user);
                            String newRfJwt = jwtProvider.generateRefreshToken(user, jwtProvider.getIssuedAt(newAccessJwt));
                             // Blacklist old token
                            return blacklistedService.save(oldToken)
                                    .then(tokenService.save(jwtProvider.rfJwtToRefreshToken(newRfJwt)))
                                    .map(refreshToken -> new AuthenticationResponse(newAccessJwt, refreshToken.getToken()))
                                    .switchIfEmpty(Mono.error(new Throwable("Error saving new token old token not found")));
                        })
                        .switchIfEmpty(Mono.error(new Throwable("User not found")))
                ).onErrorMap(error -> new Throwable(error.getMessage(), error));
    }

    private Mono<RefreshToken> verificateAndReturnRefreshToken(String jti) {
        return tokenService.findByJti(jti)
                .flatMap(
                        token -> blacklistedService.findByJti(token.getJti())
                                .hasElement()
                                .flatMap(hasElement-> hasElement
                                        ? Mono.error(new Throwable("Token is blacklisted"))
                                        : Mono.just(token)))
                .switchIfEmpty(Mono.error(new Throwable("Invalid refresh token")));
    }
}