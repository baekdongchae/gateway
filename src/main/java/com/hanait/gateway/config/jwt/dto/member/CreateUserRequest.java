package com.hanait.gateway.config.jwt.dto.member;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //스프링이 json을 파싱해 dto에 담아줄 수 있도록 함
public class CreateUserRequest {

    @NotEmpty
    private String userId;
    /**
     * @NotNull: null 비허용, "" 허용, " " 허용
     * @NotEmpty: null 비허용, "" 비허용, " " 허용
     * @NotBlank: null 비허용, "" 비허용, " " 비허용
     */
    @NotNull
    private String userPw;

}
