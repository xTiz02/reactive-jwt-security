package org.prd.reactivesecurity.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Role {

    @MongoId
    private String id;

    private String name;

    @DocumentReference(lookup="{ 'id' : ?#{_id}, 'name' : ?#{permission} }")
    private List<Permission> permissions = new ArrayList<>();


    public List<String> getPermissionsArray() {
        List<String> authorities = new ArrayList<>();
        permissions.forEach(permission -> {
            authorities.add(permission.getName());
        });
        return authorities;
    }
}