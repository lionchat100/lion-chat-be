package com.lion.be.global.util;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import java.util.Objects;

public class HttpHeaderParser {

    private HttpHeaderParser() {
        throw new IllegalStateException();
    }

    public static String parse(String authHeader, HttpHeaderType headerType) {
        if (Objects.isNull(authHeader)) {
            throw new CustomException(ErrorCode.USER_UNAUTHORIZED);
        }

        return authHeader.substring(headerType.skim.length() + 1);
    }

}
