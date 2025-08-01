package com.lion.be.auth.controller;

import com.lion.be.auth.controller.dto.CurrentUserResponse;
import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.service.UserReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserReadService userReadService;

    @GetMapping("/api/users/me")
    public ResponseEntity<CurrentUserResponse> fetchCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CurrentUserResponse response = userReadService.fetchCurrentUserResponse(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

}