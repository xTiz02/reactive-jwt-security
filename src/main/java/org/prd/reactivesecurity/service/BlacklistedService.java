package org.prd.reactivesecurity.service;

import org.prd.reactivesecurity.persistence.entity.BlacklistedToken;
import org.prd.reactivesecurity.persistence.entity.RefreshToken;
import reactor.core.publisher.Mono;

public interface BlacklistedService {
    Mono<BlacklistedToken> save(RefreshToken token);
    Mono<BlacklistedToken> findByJti(String token);

}