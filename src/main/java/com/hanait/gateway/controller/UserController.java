package com.hanait.gateway.controller;

import com.hanait.gateway.principle.UserPrinciple;
import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.dto.member.CreateUserRequest;
import com.hanait.gateway.config.jwt.dto.member.LoginUserRequest;
import com.hanait.gateway.config.jwt.dto.member.UserInfoDto;
import com.hanait.gateway.config.jwt.token.ApiResponseJson;
import com.hanait.gateway.model.User;
import com.hanait.gateway.service.UserService;
import com.hanait.gateway.util.IpAddressUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final String[] adminUrl = {"/admin/**"};
    private final String[] permitAllUrl = {"/error", "/user/login"};
    private final String[] anonymousUrl = {"/user/register"};
    
    /**
     * Generates a unique UUID for API request tracking
     * @return String UUID
     */
    private String generateRequestUUID() {
        return UUID.randomUUID().toString();
    }

    @PostMapping("/user/register")
    public ApiResponseJson register(@Valid @RequestBody CreateUserRequest request, BindingResult bindingResult) {
        String requestUUID = generateRequestUUID();
        log.info("API Call [{}] - Register request received", requestUUID);

        if (bindingResult.hasErrors()) {
            log.error("API Call [{}] - Invalid request", requestUUID);
            throw new IllegalArgumentException("잘못된 요청입니다");
        }

        User user = userService.createMember(request);
        log.info("API Call [{}] - 계정 생성 성공: {}", requestUUID, user);

        return new ApiResponseJson(
                HttpStatus.OK, Map.of("userCode", user.getUserId(), "requestId", requestUUID)
        );
    }

    @PostMapping("/user/login")
    public ApiResponseJson login(@Valid @RequestBody LoginUserRequest request, BindingResult bindingResult) {
        String requestUUID = generateRequestUUID();
        String clientIp = IpAddressUtil.getClientIpAddress();

        log.info("API Call [{}] - Login request received for user: {}", requestUUID, request.getUserId());
        
        if (bindingResult.hasErrors()) {
            log.error("API Call [{}] - Invalid login request", requestUUID);
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        TokenInfo tokenInfo = userService.loginMember(request.getUserId(), request.getUserPw());

        log.info("API Call [{}] - Token issued for user: {}", requestUUID, request.getUserId());
        log.info("clientIP [{}] - Token issued for user: {}", clientIp, request.getUserId());

        // Add the requestId to the response
        tokenInfo.setRequestId(requestUUID);
        tokenInfo.setIpAdd(clientIp);
        
        return new ApiResponseJson(HttpStatus.OK, tokenInfo);
    }

    @GetMapping("/userinfo")
    public ApiResponseJson getUserInfo(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        String requestUUID = generateRequestUUID();
        String userCode = userPrinciple.getUserCode();
        log.info("API Call [{}] - User info request for code: {}", requestUUID, userCode);

        UserInfoDto userInfoDto = userService.getUserInfo(userCode);
        
        // Add the requestId to the response
        Map<String, Object> response = Map.of(
            "userInfo", userInfoDto,
            "requestId", requestUUID
        );

        return new ApiResponseJson(HttpStatus.OK, response);
    }

    @PostMapping("/user/logout")
    public ApiResponseJson logout(@AuthenticationPrincipal UserPrinciple userPrinciple,
                                  @RequestHeader("Authorization") String authHeader) {
        String requestUUID = generateRequestUUID();
        String userCode = userPrinciple.getUserCode();

        log.info("API Call [{}] - Logout request for user code: {}", requestUUID, userCode);

        // Bearer 를 문자열에서 제외하기 위해 substring을 사용
        userService.logout(authHeader.substring(7), userCode);

        return new ApiResponseJson(HttpStatus.OK, Map.of(
            "message", "로그아웃 성공",
            "requestId", requestUUID
        ));
    }
}
