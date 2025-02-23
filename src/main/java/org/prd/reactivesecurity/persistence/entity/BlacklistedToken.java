package org.prd.reactivesecurity.persistence.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class BlacklistedToken {
    @MongoId
    private Long id;

    private String jti;

    @DBRef
    private RefreshToken refreshToken;

    private Date blacklistedAt;
}