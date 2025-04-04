package com.hanait.gateway.config.jwt.dto.member;

import com.hanait.gateway.model.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDto {
    private String userId;
    private Role role;

    @Builder
    public UserInfoDto(String userId, Role role) {
        this.userId = userId;
        this.role = role;
    }
}