package org.prd.reactivesecurity.util.mapper;

import org.prd.reactivesecurity.persistence.dto.UserDto;
import org.prd.reactivesecurity.persistence.entity.User;

public class UserMapper {

    public static UserDto mapToModel(User userDocument) {
        return new UserDto(
                userDocument.getUsername(),
                userDocument.getEmail(),
                userDocument.getPassword(),
                userDocument.getRole().getPermissionsArray()
        );
    }

    public static User mapToEntity(UserDto userDto) {
        return User.builder()
                .username(userDto.username())
                .email(userDto.email())
                .password(userDto.password())
                .build();
    }

}