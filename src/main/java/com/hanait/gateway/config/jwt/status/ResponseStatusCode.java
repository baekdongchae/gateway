package com.hanait.gateway.config.jwt.status;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseStatusCode {
    public static final int URL_NOT_FOUND = 404;
    public static final int EMAIL_NOT_VERIFIED = 410;
    public static final int LOGIN_FAILED = 430;

    public static final int OK = 200;

    public static final int WRONG_PARAMETER = 400;
    public static final int TOKEN_VALIDATION_TRY_FAILED = 401;
    public static final int TOKEN_WRONG_SIGNATURE = 402;
    public static final int TOO_MANY_REQUEST = 429;

    public static final int NO_AUTH_HEADER = 1001;
    public static final int TOKEN_EXPIRED = 1011;
    public static final int REFRESH_TOKEN_EXPIRED = 1012;
    public static final int TOKEN_HASH_NOT_SUPPORTED = 1021;

    public static final int SERVER_ERROR = 500;

    public static final int TOKEN_IS_BLACKLIST = 4012;


    public static final int TOKEN_WRONG_TYPE = 4017;
    public static final int TOKEN_REFRESHED = 4018;
}