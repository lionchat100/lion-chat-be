package com.lion.be.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.controller.dto.OnboardingLabelsResponse;
import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.controller.dto.OnboardingResponse;
import com.lion.be.user.service.UserReadService;
import com.lion.be.user.service.UserWriteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users/")
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
}
