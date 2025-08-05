package com.lion.be.user.repository.persistence.jpa;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
    SELECT u FROM User u WHERE 
    u.id != :currentUserId AND 
    u.onboardingStatus = 'COMPLETED' AND 
    (:preferredGender IS NULL OR u.gender = :preferredGender) AND 
    (:preferredMbti IS NULL OR u.mbti = :preferredMbti) AND 
    (:preferredUniversity IS NULL OR u.university = :preferredUniversity) AND 
    (:preferredPosition IS NULL OR u.position = :preferredPosition)
    ORDER BY u.createdAt DESC
    """)
    List<User> findMatchingUsers(@Param("currentUserId") Long currentUserId,
        @Param("preferredGender") Gender preferredGender,
        @Param("preferredMbti") Mbti preferredMbti,
        @Param("preferredUniversity") String preferredUniversity,
        @Param("preferredPosition") Position preferredPosition,
        Pageable pageable);

    @Query("""
    SELECT u FROM User u WHERE 
    u.id != :currentUserId AND 
    u.id NOT IN :excludeUserIds AND
    u.onboardingStatus = 'COMPLETED' AND 
    (:preferredGender IS NULL OR u.gender = :preferredGender) AND 
    (:preferredMbti IS NULL OR u.mbti = :preferredMbti) AND 
    (:preferredUniversity IS NULL OR u.university = :preferredUniversity) AND 
    (:preferredPosition IS NULL OR u.position = :preferredPosition)
    ORDER BY u.createdAt DESC
    """)
    List<User> findMatchingUsersWithExclusion(@Param("currentUserId") Long currentUserId,
        @Param("excludeUserIds") List<Long> excludeUserIds,
        @Param("preferredGender") Gender preferredGender,
        @Param("preferredMbti") Mbti preferredMbti,
        @Param("preferredUniversity") String preferredUniversity,
        @Param("preferredPosition") Position preferredPosition,
        Pageable pageable);

}
