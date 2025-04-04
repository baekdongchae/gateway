package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redis_sms_code")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
public class RedisSmsCode {

    @Id
    @Column(name = "user_phone", length = 15)
    private String userPhone;

    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

    @Builder
    public RedisSmsCode(String userPhone, String code, String expireTime) {
        this.userPhone = userPhone;
        this.code = code;
        this.expireTime = expireTime;
    }
}
