package com.hanait.gateway.config.jwt.dto.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class loginUserRequest {

    @NotEmpty
    private String userId;
    @NotEmpty
    private String userPw;
}
