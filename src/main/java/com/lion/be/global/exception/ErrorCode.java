package com.lion.be.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    TOKEN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "토큰의 권한이 없습니다."),

    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "유저의 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ONBOARDING_NOT_COMPLETED(HttpStatus.UNAUTHORIZED, "온보딩을 완료해야 해당 기능을 사용할 수 있습니다."),

    UNIVERSITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 대학교입니다."),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
