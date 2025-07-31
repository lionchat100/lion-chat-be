package com.lion.be.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken {

    private String accessToken;
    private String refreshToken;
    private Boolean isNewUser;

}
