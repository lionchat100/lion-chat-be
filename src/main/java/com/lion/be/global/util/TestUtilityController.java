package com.lion.be.global.util;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Profile("test")
public class TestUtilityController {

    private final UserJpaRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 테스트용 JWT를 대량 생성하는 API
     * @param request 생성할 토큰의 개수
     * @return 생성된 JWT 목록 (Bearer 포함)
     */
    @PostMapping("/api/test/generate-tokens")
    public ResponseEntity<List<String>> generateTokens(@RequestBody GenerateTokensRequest request) {
        // DB에서 요청된 수만큼 사용자를 조회합니다.
        Page<User> users = userRepository.findAll(PageRequest.of(0, request.getCount()));

        List<String> tokens = users.stream()
                .map(this::createTokenForUser)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tokens);
    }

    private String createTokenForUser(User user) {
        // User 엔티티로부터 Authentication 객체를 생성합니다.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
        UserPrincipal principal = new UserPrincipal(user.getId(), user.getEmail(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        // JwtTokenProvider를 사용해 토큰을 생성합니다.
        String token = jwtTokenProvider.generateAccessToken(authentication);
        return "Bearer " + token;
    }

    @Data
    static class GenerateTokensRequest {
        private int count;
    }
}
