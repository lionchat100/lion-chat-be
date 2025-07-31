package com.lion.be.auth.controller.dto;

public record AuthTokenResponse(String accessToken, boolean isNewUser) {

}
