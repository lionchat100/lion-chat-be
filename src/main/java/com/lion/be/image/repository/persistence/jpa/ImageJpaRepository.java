package com.lion.be.image.repository.persistence.jpa;

import com.lion.be.image.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {

}
