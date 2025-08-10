package com.lion.be.auth.controller.dto;

public record CurrentUserResponse(Long id, String email, String name, String imageUrl, boolean isOnboardingCompleted) {

}
