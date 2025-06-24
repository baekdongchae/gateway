package com.hanait.gateway.model;

import com.hanait.gateway.config.jwt.dto.member.UserInfoDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userCode;

    @Column(name = "user_id", unique = true, length = 50)
    private String userId;

    @Column(length = 100)
    private String userPw;

    private String phoneNumber;

    private int isActive;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String userId, String userPw, String phoneNumber, Role role) {
        this.userId = userId;
        this.userPw = userPw;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public UserInfoDto toUserInfoDte() {
        return UserInfoDto.builder()
                .userId(this.getUserId())
                .phoneNumber(this.getPhoneNumber())
                .role(this.getRole())
                .build();
    }

}
