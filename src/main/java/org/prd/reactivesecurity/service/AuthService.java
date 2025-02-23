package org.prd.reactivesecurity.service;

import org.prd.reactivesecurity.persistence.dto.AuthenticationRequest;
import org.prd.reactivesecurity.persistence.dto.AuthenticationResponse;
import org.prd.reactivesecurity.persistence.dto.UserDto;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthenticationResponse> login(AuthenticationRequest authenticationRequest);
    Mono<UserDto> register(UserDto userDto);
     Mono<AuthenticationResponse> refreshAccessToken(String refreshToken);
    Mono<AuthenticationResponse> createRefreshToken(String rf);

}