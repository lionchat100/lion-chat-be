package com.lion.be.image.repository;

import java.util.List;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.image.domain.entity.Image;
import com.lion.be.image.repository.persistence.jpa.ImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepository {

    private final ImageJpaRepository imageJpaRepository;

    @Override
    public Image save(Image image) {
        return imageJpaRepository.save(image);
    }

    @Override
    public Image fetchById(Long imageId) {
        return imageJpaRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
    }

    @Override
    public void deleteById(Long id) {
        imageJpaRepository.softDelete(id);
    }

	@Override
	public List<Image> fetchAllById(List<Long> imageIds) {
		return imageJpaRepository.findAllById(imageIds);
	}

}
