package com.lion.be.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.user.controller.dto.UserCardFilterRequest;
import com.lion.be.user.controller.dto.UserCardResponse;
import com.lion.be.user.service.UserReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user/card")
@RequiredArgsConstructor
public class UserCardController {

	private final UserReadService userReadService;

	@GetMapping
	public ResponseEntity<List<UserCardResponse>> getMatchingCards(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@ModelAttribute UserCardFilterRequest filterRequest,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) List<Long> excludeUserIds
	) {
		log.info("사용자 {}가 카드 조회 요청 - size: {}, 제외할 사용자 수: {}",
			userPrincipal.getId(), size,
			excludeUserIds != null ? excludeUserIds.size() : 0);

		List<UserCardResponse> cards = userReadService.getMatchingCards(
			userPrincipal.getId(),
			filterRequest,
			size,
			excludeUserIds
		);
		return ResponseEntity.ok(cards);
	}

}
