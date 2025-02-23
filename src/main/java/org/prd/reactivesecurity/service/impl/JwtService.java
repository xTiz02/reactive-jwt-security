package org.prd.reactivesecurity.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.prd.reactivesecurity.persistence.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.netty.http.server.HttpServerRequest;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtService {

    @Value("${security.jwt.expiration-in-minutes}")
    private Long expiration;

    @Value("${security.jwt.secret-key}")
    private String secret;


    public String generateToken(UserDetails userDetails) {

        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(issuedAt.getTime() + this.expiration);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getKey(secret))
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails, Date issuedAt) {
        Date expiration = new Date(issuedAt.getTime() + this.expiration * 2);

        return Jwts.builder()
                .header().type("JWT").and()
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .id(UUID.randomUUID().toString())
                .claims(
                        Map.of("type_token", "refresh")
                )
                .signWith(getKey(secret), Jwts.SIG.HS256)
                .compact();
    }

    public RefreshToken rfJwtToRefreshToken(String refresh) {
        Claims claims = getClaims(refresh);
        return RefreshToken.builder()
                .jti(claims.getId())
                .expiresAt(claims.getExpiration())
                .createdAt(claims.getIssuedAt())
                .token(refresh)
                .build();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date getIssuedAt(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getIssuedAt();
    }

    public String extractJti(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getId();
    }

    public boolean validate(String token){
        try {
            Jwts.parser()
                    .verifyWith(getKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("token expired");
        } catch (UnsupportedJwtException e) {
            log.warn("token unsupported");
        } catch (MalformedJwtException e) {
            log.warn("token malformed");
        } catch (SignatureException e) {
            log.warn("bad signature");
        } catch (IllegalArgumentException e) {
            log.warn("illegal args");
        }
        return false;
    }

    private SecretKey getKey(String secret) {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}