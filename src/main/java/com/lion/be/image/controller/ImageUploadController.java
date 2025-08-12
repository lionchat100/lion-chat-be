package com.lion.be.image.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.image.controller.dto.ImageUploadResponse;
import com.lion.be.image.domain.entity.Image;
import com.lion.be.image.service.ImageUploadService;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping("/api/images/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("image") MultipartFile image,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Image uploadedImage = imageUploadService.upload(image, "profile", userPrincipal.getId());
            ImageUploadResponse response = new ImageUploadResponse(uploadedImage);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
	// 리스트로 처리하는거 추가
	@PostMapping("/api/images/upload/list")
	public ResponseEntity<List<ImageUploadResponse>> uploadImageList(
		@RequestParam("images") List<MultipartFile> images,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {

		try {
			List<Image> uploadedImages = imageUploadService.uploadImages(images, "profile", userPrincipal.getId());

			List<ImageUploadResponse> responses = uploadedImages.stream()
				.map(ImageUploadResponse::new)
				.toList();

			return ResponseEntity.ok(responses);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (CustomException e) {
			return ResponseEntity.badRequest().build();
		}
	}

    @DeleteMapping("/api/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            imageUploadService.deleteImage(imageId, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (CustomException e) {
			if (e.getErrorCode() == ErrorCode.IMAGE_DELETE_ACCESS_DENIED) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			if (e.getErrorCode() == ErrorCode.IMAGE_NOT_FOUND) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    	}
	}

}
