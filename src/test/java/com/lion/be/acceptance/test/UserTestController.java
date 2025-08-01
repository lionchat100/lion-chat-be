package com.lion.be.acceptance.test;

import com.lion.be.acceptance.test.dto.UserTestRequest;
import com.lion.be.auth.controller.dto.AuthTokenResponse;
import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.auth.service.RefreshTokenService;
import com.lion.be.global.util.JwtTokenProvider;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserReadService;
import com.lion.be.user.service.UserWriteService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequiredArgsConstructor
public class UserTestController {

    private final UserReadService userReadService;
    private final UserWriteService userWriteService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/api/test/login")
    public ResponseEntity<AuthTokenResponse> testLogin(@RequestBody UserTestRequest request) {
        String email = request.email();
        String name = request.name();
        String imageUrl = request.imageUrl();
        boolean isNewUser = false;

        User user;
        try {
            // 1. UserReadService를 통해 사용자 조회
            user = userReadService.fetchByEmail(email);
        } catch (RuntimeException e) {
            // 2. 사용자가 없으면 UserWriteService를 통해 새로 생성
            User newUser = new User(name, email, imageUrl, Role.USER);
            userWriteService.save(newUser);
            user = userReadService.fetchByEmail(email); // 저장 후 다시 조회 (ID 포함)
            isNewUser = true;
        }

        // 3. Authentication 객체 생성
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRoleKey()))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                "",
                userPrincipal.getAuthorities()
        );

        // 4. 토큰 생성 및 저장
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        refreshTokenService.saveToken(user.getEmail(), refreshToken);

        // 5. 응답 반환
        AuthTokenResponse response = new AuthTokenResponse(accessToken);
        return ResponseEntity.ok(response);
    }

}
