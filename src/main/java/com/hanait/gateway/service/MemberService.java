package com.hanait.gateway.service;

import com.hanait.gateway.config.jwt.blacklist.AccessTokenBlackList;
import com.hanait.gateway.config.jwt.dto.TokenInfo;
import com.hanait.gateway.config.jwt.dto.member.CreateMemberRequest;
import com.hanait.gateway.config.jwt.dto.member.MemberInfoDto;
import com.hanait.gateway.config.jwt.token.TokenProvider;
import com.hanait.gateway.model.Member;
import com.hanait.gateway.model.Role;
import com.hanait.gateway.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$"; //해당 정규표현식을 만족하기 위해서는 최소 8자리 + 영어, 숫자, 특수문자를 모두 포함해야함.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AccessTokenBlackList accessTokenBlackList;

    //회원가입
    public Member createMember(CreateMemberRequest request) {
        checkPasswordStrength(request.getPassword());

        //이미 등록된 이메일인지 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            log.info("이미 등록된 이메일={}", request.getEmail());
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        //builder를 통해 멤버(유저 권한) 생성
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) //비밀번호 암호화
                .username(request.getUsername())
                .role(Role.ROLE_USER)
                .build();

        return memberRepository.save(member);
    }

    private void checkPasswordStrength(String password) {
        //비밀번호 정책에 맞는지 체크
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            return;
        }

        log.info("비밀번호 정책 미달");
        throw new IllegalArgumentException("비밀번호는 최소 8자리여야하고 영어, 숫자, 특수문자를 포함해야 합니다.");
    }

    //로그인
    public TokenInfo loginMember(String email, String password) {
        try {
            Member member = findMemberByEmail(email);

            checkPassword(password, member);

            return tokenProvider.createToken(member);
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    //로그아웃
    public void logout(String accessToken, String email) {
        accessTokenBlackList.setBlackList(accessToken, email);
    }

    private void checkPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.info("비밀번호가 일치하지 않음.");
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.info("계정이 존재하지 않음.");
            return new IllegalArgumentException("계정이 존재하지 않습니다.");
        });
    }

    public MemberInfoDto getUserInfo(String email) {
        return findMemberByEmail(email).toMemberInfoDte();
    }

}