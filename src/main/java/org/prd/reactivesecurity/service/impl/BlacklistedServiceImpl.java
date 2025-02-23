package org.prd.reactivesecurity.service.impl;

import org.prd.reactivesecurity.persistence.entity.BlacklistedToken;
import org.prd.reactivesecurity.persistence.entity.RefreshToken;
import org.prd.reactivesecurity.persistence.repo.BlacklistedRepository;
import org.prd.reactivesecurity.service.BlacklistedService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class BlacklistedServiceImpl implements BlacklistedService {
    private final BlacklistedRepository blacklistedRepository;

    public BlacklistedServiceImpl(BlacklistedRepository blacklistedRepository) {
        this.blacklistedRepository = blacklistedRepository;
    }

    @Override
    public Mono<BlacklistedToken> save(RefreshToken token) {
        BlacklistedToken bl = BlacklistedToken.builder()
                .jti(token.getJti())
                .refreshToken(token)
                .blacklistedAt(new Date())
                .build();
        return blacklistedRepository.save(bl);
    }

    @Override
    public Mono<BlacklistedToken> findByJti(String token) {
        return blacklistedRepository.findByJti(token);
    }
}