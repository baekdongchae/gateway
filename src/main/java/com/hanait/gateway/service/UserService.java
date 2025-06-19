package com.hanait.gateway.service;

import com.hanait.gateway.config.jwt.blacklist.AccessTokenBlackList;
import com.hanait.gateway.config.jwt.blacklist.RefreshTokenList;
import com.hanait.gateway.config.jwt.token.dto.TokenInfo;
import com.hanait.gateway.config.jwt.dto.member.CreateUserRequest;
import com.hanait.gateway.config.jwt.dto.member.UserInfoDto;
import com.hanait.gateway.config.jwt.token.TokenProvider;
import com.hanait.gateway.logging.db.LogDbChange;
import com.hanait.gateway.logging.db.LoginLogger;
import com.hanait.gateway.model.User;
import com.hanait.gateway.model.Role;
import com.hanait.gateway.repository.UserRepository;
import com.hanait.gateway.util.IpAddressUtil;
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
    private final RedisLoginService redisLoginService;
    private final LoginLogger loginLogger;

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
    public TokenInfo loginMember(TokenInfo tokenInfo, String userId, String userPw) {
        String clientIp = IpAddressUtil.getClientIpAddress();

        User user = findMemberByUserId(userId);
        Long userCode = user.getUserCode();

        try {
            if (redisLoginService.isUserBlocked(userCode)) {
                loginLogger.logBlocked(tokenInfo.getRequestId(), userCode, 5, clientIp); // MongoDB에 차단 로그 기록
                throw new IllegalStateException("해당 계정은 일시적으로 차단되었습니다.");
            }

            checkPassword(userPw, user);

            redisLoginService.clearSignInAttempts(userId);
            tokenInfo.setIpAdd(clientIp);

            return tokenProvider.createToken(user, clientIp);

        } catch (BadCredentialsException e) {
            int count = redisLoginService.increaseSignInAttempts(userCode);

            if (count >= 5) {
                // 차단된 유저는 차단 로그 기록
                loginLogger.logBlocked(tokenInfo.getRequestId(), userCode, count, clientIp);
            } else {
                // 일반 실패 로그
                loginLogger.logFailure(tokenInfo.getRequestId(), userCode, count, clientIp);
            }

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

//    private User findMemberByUserCode(Long userCode) {
//        return userRepository.findByUserCode(userCode).orElseThrow(() -> {
//            log.info("계정이 존재하지 않음.");
//            return new IllegalArgumentException("계정이 존재하지 않습니다.");
//        });
//    }

    @Transactional
    @LogDbChange(table = "user", operation = "UPDATE")
    public void updateUser(Long userCode, String newPassword) {
        User user = userRepository.findById(userCode)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.setUserPw(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}