package org.prd.reactivesecurity.persistence.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User implements UserDetails {

    @MongoId
    private String id;
    private String username;
    private String email;
    private String password;
    private boolean status;

    @DocumentReference(lookup="{'name' : ?#{target} }")
    private Role role;

    @DocumentReference(lookup="{ 'id' : ?#{_id}, 'name' : ?#{permission} }")
    private List<Permission> extraPermissions;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>(getRole().getPermissions());

        if(!getExtraPermissions().isEmpty()){
            getExtraPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getAuthority()));
            });
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status;
    }

    @Override
    public boolean isEnabled() {
        return status;
    }
}