package org.prd.reactivesecurity.persistence.repo;

import org.prd.reactivesecurity.persistence.entity.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, String> {
    Mono<RefreshToken> findByJti(String jti);
}