package com.lion.be.image.domain.entity;

import com.lion.be.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName; // 사용자가 업로드한 파일명

    @Column(nullable = false)
    private String storedFileName; // S3에 저장될 때 사용된 파일명 (UUID 포함)

    @Column(nullable = false)
    private String imageUrl; // S3에 저장된 이미지의 전체 URL

    private boolean isDeleted;

	private Long uploaderId; // 사진업로드를 누가 했는지 체크하기 위함

    public Image(String originalFileName, String storedFileName, String imageUrl, Long uploaderId) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imageUrl = imageUrl;
        this.isDeleted = false;
		this.uploaderId = uploaderId;
    }

}
