package org.prd.reactivesecurity.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Permission implements GrantedAuthority {
    @MongoId
    private String id;

    private String name;

    private String description;

    @Override
    public String getAuthority() {
        return name;
    }
}