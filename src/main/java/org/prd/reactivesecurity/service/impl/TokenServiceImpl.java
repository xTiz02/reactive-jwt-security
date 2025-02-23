package org.prd.reactivesecurity.service.impl;

import org.prd.reactivesecurity.persistence.entity.RefreshToken;
import org.prd.reactivesecurity.persistence.repo.RefreshTokenRepository;
import org.prd.reactivesecurity.service.BlacklistedService;
import org.prd.reactivesecurity.service.TokenService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;

    }

    @Override
    public Mono<RefreshToken> findByJti(String jti) {
        return refreshTokenRepository.findByJti(jti);
    }

    @Override
    public Mono<RefreshToken> save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }
}