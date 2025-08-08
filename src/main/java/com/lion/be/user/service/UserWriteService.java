package com.lion.be.user.service;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.controller.dto.OnboardingResponse;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.University;
import com.lion.be.user.domain.entity.dto.OnboardingData;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserWriteService {

    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public OnboardingResponse completeUserOnboarding(Long userId, OnboardingRequest request, University university) {
        User user = userRepository.fetchById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        OnboardingData data = OnboardingData.from(request);
        user.completeOnboarding(data, university);

        userRepository.save(user);

        return OnboardingResponse.success(userId);
    }
}
