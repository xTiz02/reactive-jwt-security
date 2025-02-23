package org.prd.reactivesecurity.service;

import org.prd.reactivesecurity.persistence.entity.RefreshToken;
import reactor.core.publisher.Mono;


public interface TokenService {
    Mono<RefreshToken> findByJti(String jti);
    Mono<RefreshToken> save(RefreshToken refreshToken);
}