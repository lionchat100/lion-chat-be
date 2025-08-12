package com.lion.be.image.controller.dto;

import com.lion.be.image.domain.entity.Image;
import lombok.Getter;

@Getter
public class ImageUploadResponse {

    private final Long imageId;
    private final String imageUrl;

    public ImageUploadResponse(Image image) {
        this.imageId = image.getId();
        this.imageUrl = image.getImageUrl();
    }

}