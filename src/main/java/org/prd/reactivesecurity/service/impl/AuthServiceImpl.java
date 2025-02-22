package org.prd.reactivesecurity.service.impl;

import org.prd.reactivesecurity.persistence.dto.AuthenticationRequest;
import org.prd.reactivesecurity.persistence.dto.AuthenticationResponse;
import org.prd.reactivesecurity.persistence.dto.UserDto;
import org.prd.reactivesecurity.persistence.entity.User;
import org.prd.reactivesecurity.persistence.repo.RoleRepository;
import org.prd.reactivesecurity.persistence.repo.UserRepository;
import org.prd.reactivesecurity.service.AuthService;
import org.prd.reactivesecurity.util.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, JwtService jwtProvider, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.roleRepository = roleRepository;

    }


    @Override
    public Mono<AuthenticationResponse> login(AuthenticationRequest dto) {

        return userRepository.findByUsername(dto.username())
                .filter(user -> passwordEncoder.matches(dto.password(), user.getPassword()))
                .map(user -> new AuthenticationResponse(jwtProvider.generateToken(user),"Not Implemented"))
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
                                        .map(UserMapper::mapToModel);
                            })
                            .switchIfEmpty(Mono.error(new Throwable("Role not found")));
                });
    }
}