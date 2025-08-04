package com.lion.be.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.controller.dto.OnboardingResponse;
import com.lion.be.user.domain.entity.University;
import com.lion.be.user.service.UniversityReadService;
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
	private final UniversityReadService universityReadService;

	@PatchMapping("/onboarding")
	public ResponseEntity<OnboardingResponse> onboarding (
			@AuthenticationPrincipal UserPrincipal userPrincipal,
			@Valid @RequestBody OnboardingRequest onboardingRequest
	) {
		University university = universityReadService.getByUniversityName(onboardingRequest.getUniversityName());

		OnboardingResponse response = userWriteService.completeUserOnboarding(
				userPrincipal.getId(),
				onboardingRequest,
				university
		);
		return ResponseEntity.ok(response);
	}
}
