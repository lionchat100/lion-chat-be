package com.lion.be.user.service;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.user.domain.entity.University;
import com.lion.be.user.repository.UniversityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UniversityReadService {

	private final UniversityRepository universityRepository;

	public University getByUniversityName(String universityName) {
		return universityRepository.fetchByName(universityName)
			.orElseThrow(() -> new CustomException(ErrorCode.UNIVERSITY_NOT_FOUND));
	}
}
