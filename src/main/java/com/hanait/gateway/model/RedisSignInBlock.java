package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redis_sign_in_block")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RedisSignInBlock {

    @Id
    @Column(name = "user_id", length = 20)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

    @Builder
    public RedisSignInBlock(String userId, String expireTime) {
        this.userId = userId;
        this.expireTime = expireTime;
    }
}
