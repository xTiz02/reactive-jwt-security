package org.prd.reactivesecurity.web.handler;

import org.prd.reactivesecurity.persistence.dto.AuthenticationRequest;
import org.prd.reactivesecurity.persistence.dto.AuthenticationResponse;
import org.prd.reactivesecurity.persistence.dto.UserDto;
import org.prd.reactivesecurity.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {
    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Hello"), String.class);
    }

    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request.bodyToMono(UserDto.class)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authService.register(dto), UserDto.class))
                .onErrorResume(e -> ServerResponse.badRequest().body(Mono.just(e.getMessage()), String.class));
    }

    public Mono<ServerResponse> logIn(ServerRequest request) {
        return request.bodyToMono(AuthenticationRequest.class)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authService.login(dto), AuthenticationResponse.class))
                .onErrorResume(e -> ServerResponse.badRequest().body(Mono.just(e.getMessage()), String.class));
    }

    public Mono<ServerResponse> generateAccessToken(ServerRequest request) {
        String refreshToken = request.queryParam("refresh-token").orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.refreshAccessToken(refreshToken), AuthenticationResponse.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(Mono.just(e.getMessage()), String.class));
    }

    public Mono<ServerResponse> createRefreshToken(ServerRequest request) {
        String refreshToken = request.queryParam("refresh-token").orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.createRefreshToken(refreshToken), AuthenticationResponse.class)
                .onErrorResume(e -> ServerResponse.badRequest().body(Mono.just(e.getMessage()), String.class));
    }

}