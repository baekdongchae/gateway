package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redis_sms_count")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
public class RedisSmsCount {

    @Id
    @Column(name = "user_phone", length = 15)
    private String userPhone;

    @Column(name = "count")
    private Integer count;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

    @Builder
    public RedisSmsCount(String userPhone, Integer count, String expireTime) {
        this.userPhone = userPhone;
        this.count = count;
        this.expireTime = expireTime;
    }
}
