package com.lion.be.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lion.be.admin.controller.dto.BanUserRequest;
import com.lion.be.admin.controller.dto.BanUserResponse;
import com.lion.be.admin.controller.dto.UnbanUserRequest;
import com.lion.be.admin.controller.dto.UnbanUserResponse;
import com.lion.be.admin.service.AdminUserWriteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final AdminUserWriteService adminUserWriteService;

	@PostMapping("/ban")
	public ResponseEntity<BanUserResponse> banUser(
		@Valid @RequestBody BanUserRequest request
		) {
		BanUserResponse response = adminUserWriteService.banUser(request);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/unban")
	public ResponseEntity<UnbanUserResponse> unbanUser(
		@Valid @RequestBody UnbanUserRequest request
	){
		UnbanUserResponse response = adminUserWriteService.unbanUser(request);
		return ResponseEntity.ok(response);
	}
}
