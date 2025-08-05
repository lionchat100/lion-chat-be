package com.lion.be.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.lion.be.user.domain.entity.University;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UniversityJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UniversityRepositoryImpl implements UniversityRepository {

	private final UniversityJpaRepository universityJpaRepository;

	@Override
	public Optional<University> fetchByName(String name) {
		return universityJpaRepository.findByName(name);
	}
}
