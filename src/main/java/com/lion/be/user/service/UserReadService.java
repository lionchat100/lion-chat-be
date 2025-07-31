package com.lion.be.user.service;

import com.lion.be.auth.controller.dto.CurrentUserResponse;
import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReadService {

    private final UserRepository userRepository;

    public User fetchByEmail(String email) {
        return userRepository.fetchByEmail(email)
                .orElseThrow(() -> new RuntimeException("fetchByEmail"));
    }

    public CurrentUserResponse fetchCurrentUserResponse(String email, UserPrincipal userPrincipal) {
        User user = fetchByEmail(email);
        return CurrentUserResponse.builder()
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .name(user.getName())
                .picture(user.getImageUrl())
                .authorities(userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .build();
    }

    public User fetchById(Long userId) {
        return userRepository.fetchById(userId)
                .orElseThrow(() -> new RuntimeException("fetchById"));
    }

}
