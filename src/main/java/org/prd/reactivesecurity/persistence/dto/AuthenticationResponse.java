package org.prd.reactivesecurity.persistence.dto;

public record AuthenticationResponse(
        String jwt,
        String refreshToken
) {
}