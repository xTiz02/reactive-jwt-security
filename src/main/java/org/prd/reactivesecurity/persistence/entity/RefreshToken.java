package org.prd.reactivesecurity.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class RefreshToken {

    @Id
    private Long id;

    private String jti;

    private String token;

    private Date createdAt;

    private Date expiresAt;

    @DBRef
    private User user;
}