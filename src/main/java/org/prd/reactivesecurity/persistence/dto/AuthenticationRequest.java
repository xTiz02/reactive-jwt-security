package org.prd.reactivesecurity.persistence.dto;

public record AuthenticationRequest(
        String username,
        String password
) {
}