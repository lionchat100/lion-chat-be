package com.lion.be.user.repository;

import java.util.Optional;

import com.lion.be.user.domain.entity.University;

public interface UniversityRepository  {

	Optional<University> fetchByName(String name);

	University save(University university);
}

