package com.lion.be.image.repository;

import java.util.List;

import com.lion.be.image.domain.entity.Image;

public interface ImageRepository {

    Image save(Image image);

    Image fetchById(Long imageId);

    void deleteById(Long id);

	List<Image> fetchAllById(List<Long> imageIds);
}
