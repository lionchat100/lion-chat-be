package com.lion.be.user.repository.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lion.be.user.domain.entity.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

	boolean existsByNickname(String nickname);
}
