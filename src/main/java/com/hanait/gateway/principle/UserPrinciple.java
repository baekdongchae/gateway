package com.hanait.gateway.principle;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * SecurityContext에 저장되는 사용자의 인증 정보를 저장하는 클래스
 * UserPrincple을 SecurityContextHolder에 저장해 인증 정보 유지
 */

@Getter
public class UserPrinciple extends User {   //spinrg security의 User class 상속

    private static final String PASSWORD_ERASED_VALUE = "[PASSWORD_ERASED]";
    private final String userCode;

    public UserPrinciple(String userCode, Collection<? extends GrantedAuthority> authorities) {
        super(userCode, PASSWORD_ERASED_VALUE, authorities);
        this.userCode = userCode;
    }

    @Override
    public String toString() {
        return "UserPrinciple(" +
                "userCode=" + userCode +
                " role=" + getAuthorities() +
                ')';
    }
}
