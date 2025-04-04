package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redis_token_cache")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RedisTokenCache {

    @Id
    @Column(name = "user_code")
    private Long userCode;

    @OneToOne
    @JoinColumn(name = "user_code")
    private User user;

    @Column(name = "access_token", columnDefinition = "text")
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "text")
    private String refreshToken;

    @Builder
    public RedisTokenCache(User user, String accessToken, String refreshToken) {
        this.userCode = user.getUserCode();
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
