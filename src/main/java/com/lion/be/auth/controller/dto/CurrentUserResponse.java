package com.lion.be.auth.controller.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CurrentUserResponse(Long id, String email, String name,
                                  String picture, List<String> authorities) {

}
