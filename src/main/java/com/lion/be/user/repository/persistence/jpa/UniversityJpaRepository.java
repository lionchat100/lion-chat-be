package com.lion.be.user.repository.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lion.be.user.domain.entity.University;

public interface UniversityJpaRepository extends JpaRepository<University, Long> {

	Optional<University> findByName(String name);

}
