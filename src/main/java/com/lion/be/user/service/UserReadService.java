package com.lion.be.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.auth.controller.dto.CurrentUserResponse;
import com.lion.be.global.exception.OnboardingNotCompletedException;
import com.lion.be.user.controller.dto.UserCardFilterRequest;
import com.lion.be.user.controller.dto.UserCardResponse;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReadService {

    private final UserRepository userRepository;

    public User fetchByEmail(String email) {
        return userRepository.fetchByEmail(email)
                .orElseThrow(() -> new RuntimeException("fetchByEmail"));
    }

    public CurrentUserResponse fetchCurrentUserResponse(Long userId) {
        User user = fetchById(userId);
        return new CurrentUserResponse(user.getId(), user.getEmail(), user.getName(), user.getImageUrl());
    }

    public User fetchById(Long userId) {
        return userRepository.fetchById(userId)
                .orElseThrow(() -> new RuntimeException("fetchById"));
    }

    public List<UserCardResponse> getMatchingCards(
        Long currentUserId,
        UserCardFilterRequest filterRequest,
        int size,
        List<Long> excludeUserIds
    ) {
        validateOnboardingCompleted(currentUserId);

        List<User> matchingUsers = userRepository.findMatchingUsersExcluding(
            currentUserId,
            filterRequest,
            size,
            excludeUserIds
        );

        return matchingUsers.stream()
            .map(UserCardResponse::from)
            .toList();
    }

    private void validateOnboardingCompleted(Long userId) {
        User user = fetchById(userId);

        if (!user.isOnboardingCompleted()) {
            throw new OnboardingNotCompletedException();
        }
    }
}
