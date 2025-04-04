package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redis_sign_in_count")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RedisSignInCount {

    @Id
    @Column(name = "user_id", length = 20)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "count")
    private Integer count;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

    @Builder
    public RedisSignInCount(String userId, Integer count, String expireTime) {
        this.userId = userId;
        this.count = count;
        this.expireTime = expireTime;
    }
}
