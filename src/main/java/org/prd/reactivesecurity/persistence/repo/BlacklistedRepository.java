package org.prd.reactivesecurity.persistence.repo;

import org.prd.reactivesecurity.persistence.entity.BlacklistedToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BlacklistedRepository extends ReactiveCrudRepository<BlacklistedToken, String> {
    Mono<BlacklistedToken> findByJti(String jti);
}