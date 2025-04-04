package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redis_sms_block")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
public class RedisSmsBlock {

    @Id
    @Column(name = "user_phone", length = 15)
    private String userPhone;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

    @Builder
    public RedisSmsBlock(String userPhone, String expireTime) {
        this.userPhone = userPhone;
        this.expireTime = expireTime;
    }
}
