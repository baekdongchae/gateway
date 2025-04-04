package com.hanait.gateway.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "user_code")
    private Long userCode;

    @OneToOne
    @JoinColumn(name = "user_code")
    private User user;

    @Column(name = "token", columnDefinition = "text")
    private String token;

    @Column(name = "update_time", length = 20)
    private String updateTime;

    @Builder
    public RefreshToken(User user, String token, String updateTime) {
        this.userCode = user.getUserCode();
        this.user = user;
        this.token = token;
        this.updateTime = updateTime;
    }
}
