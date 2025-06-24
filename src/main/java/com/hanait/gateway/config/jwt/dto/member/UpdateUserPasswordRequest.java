package com.hanait.gateway.config.jwt.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserPasswordRequest {

    @NotEmpty
    private Long userCode;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String currentPassword;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String newPassword;
}
