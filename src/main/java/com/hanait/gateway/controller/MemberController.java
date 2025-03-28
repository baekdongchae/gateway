package com.hanait.gateway.controller;

import com.hanait.gateway.principle.UserPrinciple;
import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.dto.member.CreateMemberRequest;
import com.hanait.gateway.config.jwt.dto.member.LoginMemberRequest;
import com.hanait.gateway.config.jwt.dto.member.MemberInfoDto;
import com.hanait.gateway.config.jwt.token.ApiResponseJson;
import com.hanait.gateway.model.Member;
import com.hanait.gateway.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final String[] adminUrl = {"/admin/**"};
    private final String[] permitAllUrl = {"/error", "/user/login"};
    private final String[] anonymousUrl = {"/user/register"};

    @PostMapping("/user/register")
    public ApiResponseJson register(@Valid @RequestBody CreateMemberRequest request, BindingResult bindingResult) { //@Valid를 통해 검증한 결과는 BindingResult를 통해 받아볼 수 있음

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다");
        }

        Member member = memberService.createMember(request);
        log.info("계정 생성 성공: {}", member);

        return new ApiResponseJson(
                HttpStatus.OK, Map.of("email", member.getEmail(), "username", member.getUsername())
        );
    }

    @PostMapping("/user/login")
    public ApiResponseJson login(@Valid @RequestBody LoginMemberRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        TokenInfo tokenInfo = memberService.loginMember(request.getEmail(), request.getPassword());

        log.info("Token issued: {}", tokenInfo);

        return new ApiResponseJson(HttpStatus.OK, tokenInfo);
    }

    @GetMapping("/userinfo")
    public ApiResponseJson getUserInfo(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        String email = userPrinciple.getEmail();
        log.info("요청 이메일 : {}", email);

        MemberInfoDto memberInfoDto = memberService.getUserInfo(email);

        return new ApiResponseJson(HttpStatus.OK, memberInfoDto);
    }

    @PostMapping("/user/logout")
    public ApiResponseJson logout(@AuthenticationPrincipal UserPrinciple userPrinciple,
                                  @RequestHeader("Authorization") String authHeader) {
        String email = userPrinciple.getEmail();

        log.info("로그아웃 이메일: {}", email);

        // Bearer 를 문자열에서 제외하기 위해 substring을 사용
        memberService.logout(authHeader.substring(7), email);

        return new ApiResponseJson(HttpStatus.OK, "로그아웃 성공");
    }
}

