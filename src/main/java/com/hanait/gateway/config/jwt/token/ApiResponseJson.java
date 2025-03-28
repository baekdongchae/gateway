package com.hanait.gateway.config.jwt.token;


import com.hanait.gateway.config.jwt.status.ResponseStatusCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * 응답을 API 스펙에 맞게 JSON으로 변환하기 위한 클래스
 */
@Getter
@ToString
@NoArgsConstructor
public class ApiResponseJson {
    public HttpStatus httpStatus;
    public int code;
    public Object data;

    public ApiResponseJson(HttpStatus httpStatus, int code, Object data) { //에러 발생 시
        this.httpStatus = httpStatus;
        this.code = code;
        this.data = data;
    }

    public ApiResponseJson(HttpStatus httpStatus, Object data) { //성공 시
        this.httpStatus = httpStatus;
        this.code = ResponseStatusCode.OK;
        this.data = data;
    }

}
