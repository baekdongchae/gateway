package com.hanait.gateway.config.jwt.dto.member;

import com.hanait.gateway.model.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoDto {
    private String email;
    private String username;
    private Role role;

    @Builder
    public MemberInfoDto(String email, String username, Role role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }
}