package com.lion.be.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.controller.dto.OnboardingResponse;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.dto.OnboardingData;
import com.lion.be.user.repository.UserRepository;
import com.lion.be.usercard.util.UserCardFilterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserWriteService {

    private final UserRepository userRepository;
    private final UserCardFilterUtil userCardFilterUtil;

    public void save(User user) {
        userRepository.save(user);
    }

    public OnboardingResponse completeUserOnboarding(Long userId, OnboardingRequest request) {
        User user = userRepository.fetchById(userId)
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        OnboardingData data = OnboardingData.from(request);
        user.completeOnboarding(data);

        // 온보딩 완료와 함께 클러스터 배정
        Integer clusterId = assignClusterToNewUser(user);
        user.assignToCluster(clusterId);

        userRepository.save(user);

        return OnboardingResponse.success(userId);
    }

    /**
     * UserCardFilterUtil의 클러스터링 로직을 활용해서 신규 사용자 클러스터 배정
     */
    private Integer assignClusterToNewUser(User newUser) {
        return userCardFilterUtil.assignNewUserToCluster(newUser);
    }
}
