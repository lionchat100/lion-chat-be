package com.lion.be.user.controller;

import com.lion.be.global.util.HttpHeaderParser;
import com.lion.be.global.util.HttpHeaderType;
import com.lion.be.user.controller.dto.UserIdResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.controller.dto.OnboardingLabelsResponse;
import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.controller.dto.OnboardingResponse;
import com.lion.be.user.controller.dto.UserProfileUpdateRequest;
import com.lion.be.user.controller.dto.UserProfileUpdateResponse;
import com.lion.be.user.service.UserReadService;
import com.lion.be.user.service.UserWriteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserWriteService userWriteService;
	private final UserReadService userReadService;

	@PatchMapping("/onboarding")
	public ResponseEntity<OnboardingResponse> onboarding (
			@AuthenticationPrincipal UserPrincipal userPrincipal,
			@Valid @RequestBody OnboardingRequest onboardingRequest
	) {
		OnboardingResponse response = userWriteService.completeUserOnboarding(
				userPrincipal.getId(),
				onboardingRequest
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/onboarding/labels")
	public ResponseEntity<OnboardingLabelsResponse> getOnboardinglabels(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	){
		OnboardingLabelsResponse response = userReadService.getOnboardingOptions(
			userPrincipal
		);
		return ResponseEntity.ok(response);
	}

    @GetMapping("/id")
    public ResponseEntity<UserIdResponse> fetchUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String accessToken = HttpHeaderParser.parse(authHeader, HttpHeaderType.AUTH);
        UserIdResponse response = userReadService.fetchUserId(accessToken);
        return ResponseEntity.ok(response);
    }

	@GetMapping("/check-nickname/{nickname}")
	public ResponseEntity<Boolean> checkNickname(
		@PathVariable String nickname
	) {
		boolean isDuplicate = userReadService.existsByNickname(nickname);

		return ResponseEntity.ok(!isDuplicate);
	}

	@PatchMapping("/update")
	public ResponseEntity<UserProfileUpdateResponse> updateUserProfile(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody UserProfileUpdateRequest userProfileRequest
	){
		UserProfileUpdateResponse response = userWriteService.updateUserProfile(userPrincipal, userProfileRequest);

		return ResponseEntity.ok(response);
	}

}
