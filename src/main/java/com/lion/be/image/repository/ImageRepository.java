package com.lion.be.image.repository;

import java.util.List;
import java.util.Optional;

import com.lion.be.image.domain.entity.Image;

public interface ImageRepository {

    Image save(Image image);

    Image fetchById(Long imageId);

    void deleteById(Long id);

	List<Image> fetchAllById(List<Long> imageIds);

    List<Image> fetchAllByUserId(List<Long> userIds);

    Optional<Image> fetchByUserId(Long userId);
}
