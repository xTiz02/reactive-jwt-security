package org.prd.reactivesecurity.persistence.dto;

import java.util.List;

public record  UserDto(
        String username,
        String email,
        String password,
        List<String> roles
) {
}