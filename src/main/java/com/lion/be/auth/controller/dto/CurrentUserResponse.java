package com.lion.be.auth.controller.dto;

import lombok.Builder;

public record CurrentUserResponse(Long id, String email, String name, String imageUrl) {

}
