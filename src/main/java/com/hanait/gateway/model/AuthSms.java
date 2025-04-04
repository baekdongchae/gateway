package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_sms")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthSms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Integer authId;

    @ManyToOne
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @Column(name = "auth_code", length = 10)
    private String authCode;

    @ManyToOne
    @JoinColumn(name = "is_used", nullable = false)
    private DefaultType isUsed;

    @Column(name = "expire_time", length = 20)
    private String expireTime;

    @Column(name = "create_time", length = 20)
    private String createTime;

    @Builder
    public AuthSms(User user, String authCode, DefaultType isUsed, 
                  String expireTime, String createTime) {
        this.user = user;
        this.authCode = authCode;
        this.isUsed = isUsed;
        this.expireTime = expireTime;
        this.createTime = createTime;
    }
}
