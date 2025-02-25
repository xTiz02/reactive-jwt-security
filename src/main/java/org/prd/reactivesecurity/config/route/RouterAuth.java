package org.prd.reactivesecurity.config.route;

import org.prd.reactivesecurity.web.handler.AuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterAuth {
    private static final String PATH = "auth/";

    @Bean
    public RouterFunction<ServerResponse> helloRoute(AuthHandler authHandler) {
        return RouterFunctions.route()
                .GET("hello", authHandler::hello)
                .POST(PATH + "signup", authHandler::signUp)
                .POST(PATH + "login", authHandler::logIn)
                .GET(PATH + "refresh-token", authHandler::generateAccessToken)
                .GET(PATH + "refresh-token/new", authHandler::createRefreshToken)
                .build();
    }
}