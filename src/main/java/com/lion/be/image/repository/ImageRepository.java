package com.lion.be.image.repository;

import com.lion.be.image.domain.entity.Image;

public interface ImageRepository {

    Image save(Image image);

    Image fetchById(Long imageId);

}
