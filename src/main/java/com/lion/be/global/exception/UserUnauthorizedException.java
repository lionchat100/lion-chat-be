package com.lion.be.global.exception;

public class UserUnauthorizedException extends CustomException {

    public UserUnauthorizedException() {
        super(ErrorCode.USER_UNAUTHORIZED);
    }

}
