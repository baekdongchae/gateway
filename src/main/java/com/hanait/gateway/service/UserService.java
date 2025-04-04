package com.hanait.gateway.service;

import com.hanait.gateway.config.jwt.blacklist.AccessTokenBlackList;
import com.hanait.gateway.config.jwt.blacklist.RefreshTokenList;
import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.dto.member.CreateUserRequest;
import com.hanait.gateway.config.jwt.dto.member.UserInfoDto;
import com.hanait.gateway.config.jwt.token.TokenProvider;
import com.hanait.gateway.model.User;
import com.hanait.gateway.model.Role;
import com.hanait.gateway.repository.UserRepository;
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
public class UserService {
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{5,20}$"; //해당 정규표현식을 만족하기 위해서는 최소 8자리 + 영어, 숫자, 특수문자를 모두 포함해야함.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AccessTokenBlackList accessTokenBlackList;
    private final RefreshTokenList refreshTokenList;

    //회원가입
    public User createMember(CreateUserRequest request) {
        checkPasswordStrength(request.getUserPw());

        //이미 등록된 유저코드인지 체크
        if (userRepository.existsByUserId(request.getUserId())) {
            log.info("이미 등록된 유저코드={}", request.getUserId());
            throw new IllegalArgumentException("이미 등록된 유저코드입니다.");
        }

        //builder를 통해 멤버(유저 권한) 생성
        User user = User.builder()
                .userId(request.getUserId())
                .userPw(passwordEncoder.encode(request.getUserPw())) //비밀번호 암호화
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    private void checkPasswordStrength(String userPw) {
        //비밀번호 정책에 맞는지 체크
        if (PASSWORD_PATTERN.matcher(userPw).matches()) {
            return;
        }

        log.info("비밀번호 정책 미달");
        throw new IllegalArgumentException("비밀번호는 최소 8자리여야하고 영어, 숫자, 특수문자를 포함해야 합니다.");
    }

    //로그인
    public TokenInfo loginMember(String userId, String userPw) {
        try {
            User user = findMemberByUserId(userId);

            checkPassword(userPw, user);

            return tokenProvider.createToken(user);
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    //로그아웃
    public void logout(String accessToken, String userCode) {
        accessTokenBlackList.setBlackList(accessToken, userCode);
    }

    private void checkPassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getUserPw())) {
            log.info("비밀번호가 일치하지 않음.");
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }

    private User findMemberByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> {
            log.info("계정이 존재하지 않음.");
            return new IllegalArgumentException("계정이 존재하지 않습니다.");
        });
    }

    public UserInfoDto getUserInfo(String userId) {
        return findMemberByUserId(userId).toUserInfoDte();
    }

}