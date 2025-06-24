package com.hanait.gateway.service;

import com.hanait.gateway.config.jwt.blacklist.AccessTokenBlackList;
import com.hanait.gateway.config.jwt.blacklist.RefreshTokenList;
import com.hanait.gateway.config.jwt.dto.member.UpdateUserPasswordRequest;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$"; //해당 정규표현식을 만족하기 위해서는 최소 8자리 + 영어, 숫자, 특수문자를 모두 포함해야함.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AccessTokenBlackList accessTokenBlackList;
    private final RedisLoginService redisLoginService;
    private final LoginLogger loginLogger;
    private final RedisTemplate<String, Object> redisTemplate;

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
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    private void checkPasswordStrength(String userPw) {
        if (userPw == null || !PASSWORD_PATTERN.matcher(userPw).matches()) {
            log.info("비밀번호 정규식 불일치");
            throw new IllegalArgumentException("비밀번호는 8~20자이며, 영어/숫자/특수문자를 포함해야 합니다.");
        }

        if (containsSequentialChars(userPw)) {
            log.info("연속된 문자 또는 숫자 포함");
            throw new IllegalArgumentException("비밀번호에 연속된 문자 또는 숫자는 사용할 수 없습니다.");
        }
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

    public String findUserIdByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(User::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 등록된 사용자가 없습니다."));
    }

//    private User findMemberByUserCode(Long userCode) {
//        return userRepository.findByUserCode(userCode).orElseThrow(() -> {
//            log.info("계정이 존재하지 않음.");
//            return new IllegalArgumentException("계정이 존재하지 않습니다.");
//        });
//    }

    @Transactional
    @LogDbChange(table = "user", operation = "UPDATE")
    public boolean updateUserPassword(UpdateUserPasswordRequest request) {
        User user = userRepository.findById(request.getUserCode())
                .orElseThrow(() -> new RuntimeException("사용자없음"));

        //현재 비밀전호 일치 확인
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getUserPw())) {
            log.info("현재 비밀번호 불일치, userCode={}", request.getUserCode());
            return false;
        }

        // 새 비밀번호 = 현재 비밀번호 인지 검사
        if (passwordEncoder.matches(request.getNewPassword(), user.getUserPw())) {
            log.info("새 비밀번호가 기존 비밀번호와 동일함");
            return false;
        }

        //새 비밀번호 유효성 검사
        if(!isValidPassword(request.getNewPassword())){
            log.info("새 비밀번호 유효성 실패");
            return false;
        }

        // 비밀번호 업데이트
        user.setUserPw(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        //Redis 토큰 삭제
        redisTemplate.delete("ACCESS_TOKEN:" + user.getUserCode()); // or your actual key pattern
        redisTemplate.delete("REFRESH_TOKEN:" + user.getUserCode());

        //성공 반환
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        }
        return !containsSequentialChars(password);
    }

    private boolean containsSequentialChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);

            // 예: abc, 123
            if ((c2 == c1 + 1) && (c3 == c2 + 1)) {
                return true;
            }

            // 예: aaa, 111
            if ((c1 == c2) && (c2 == c3)) {
                return true;
            }
        }
        return false;
    }
}