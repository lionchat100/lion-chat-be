package com.lion.be.image.repository.persistence.jpa;

import com.lion.be.image.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {

    @Modifying
    @Query("UPDATE Image i "
            + "SET i.isDeleted = true "
            + "WHERE i.id = :id")
    void softDelete(@Param("id") Long id);

    @Query("""
    select i
    from Image i
    join UserPhoto up on up.image.id = i.id and up.orderIndex = 1
    where i.uploaderId in :userIds
""")
    List<Image> fetchByUserIds(List<Long> userIds);

    @Query("""
    select i
    from Image i
    join UserPhoto up on up.image.id = i.id and up.orderIndex = 1
    where i.uploaderId in :userId
""")
    Optional<Image> fetchByUserId(Long userId);
}
