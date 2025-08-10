package com.lion.be.user.domain.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;

@Component
public class UserVectorizer {

	// MBTI 호환성 맵 (한 번만 초기화)
	private static final Map<Mbti, Map<String, List<Mbti>>> COMPATIBILITY_MAP = createCompatibilityMap();

	/**
	 * User를 9차원 벡터로 변환 (Weka용)
	 * [E/I, S/N, T/F, J/P, BACKEND, FRONTEND, UX_UI, PM, FULLSTACK]
	 */
	public double[] vectorize(User user) {
		validateUser(user);

		double[] vector = new double[9];

		// MBTI 4차원
		double[] mbtiBinary = getMbtiBinary(user.getMbti());
		System.arraycopy(mbtiBinary, 0, vector, 0, 4);

		// Position 5차원
		double[] positionVector = getPositionVector(user.getPosition());
		System.arraycopy(positionVector, 0, vector, 4, 5);

		return vector;
	}

	/**
	 * MBTI를 4차원 이진 벡터로 변환
	 */
	public double[] getMbtiBinary(Mbti mbti) {
		String mbtiStr = mbti.name();
		return new double[]{
			mbtiStr.charAt(0) == 'E' ? 1 : 0,  // E/I
			mbtiStr.charAt(1) == 'S' ? 1 : 0,  // S/N
			mbtiStr.charAt(2) == 'T' ? 1 : 0,  // T/F
			mbtiStr.charAt(3) == 'J' ? 1 : 0   // J/P
		};
	}

	/**
	 * Position을 5차원 벡터로 변환
	 */
	public double[] getPositionVector(Position position) {
		double[] vector = new double[5];

		switch (position) {
			case BACKEND -> {
				vector[0] = 1.0;   // BACKEND (자기 자신)
				vector[1] = 0.3;   // FRONTEND (일부 공통 기술)
				vector[2] = 0.1;   // UX_UI (협업 정도)
				vector[3] = 0.2;   // PM (협업 정도)
				vector[4] = 0.8;   // FULLSTACK (높은 유사도)
			}
			case FRONTEND -> {
				vector[0] = 0.3;   // BACKEND
				vector[1] = 1.0;   // FRONTEND (자기 자신)
				vector[2] = 0.6;   // UX_UI (UI 관련 협업)
				vector[3] = 0.3;   // PM
				vector[4] = 0.7;   // FULLSTACK
			}
			case UX_UI -> {
				vector[0] = 0.1;   // BACKEND
				vector[1] = 0.6;   // FRONTEND
				vector[2] = 1.0;   // UX_UI (자기 자신)
				vector[3] = 0.4;   // PM (기획 협업)
				vector[4] = 0.4;   // FULLSTACK
			}
			case PM -> {
				vector[0] = 0.2;   // BACKEND
				vector[1] = 0.3;   // FRONTEND
				vector[2] = 0.4;   // UX_UI
				vector[3] = 1.0;   // PM (자기 자신)
				vector[4] = 0.3;   // FULLSTACK
			}
			case FULLSTACK -> {
				vector[0] = 0.8;   // BACKEND
				vector[1] = 0.7;   // FRONTEND
				vector[2] = 0.4;   // UX_UI
				vector[3] = 0.3;   // PM
				vector[4] = 1.0;   // FULLSTACK (자기 자신)
			}
		}

		return vector;
	}

	/**
	 * MBTI 호환성 계산 (실제 MBTI 호환성 표 기반)
	 */
	public double calculateMbtiCompatibility(User user1, User user2) {
		Mbti mbti1 = user1.getMbti();
		Mbti mbti2 = user2.getMbti();

		// 같은 MBTI면 최고 호환성
		if (mbti1 == mbti2) {
			return 1.0;
		}

		Map<String, List<Mbti>> compatibilityInfo = COMPATIBILITY_MAP.get(mbti1);
		if (compatibilityInfo == null) {
			return 0.5; // 기본값
		}

		// 찰떡궁합 체크
		if (compatibilityInfo.get("찰떡궁합").contains(mbti2)) {
			return 0.9;
		}

		// 충돌 잦은 궁합 체크
		if (compatibilityInfo.get("충돌잦은궁합").contains(mbti2)) {
			return 0.1;
		}

		// 그 외는 무난한 관계
		return 0.5;
	}

	/**
	 * MBTI 호환성 맵 생성 (표 데이터 기반)
	 */
	private static Map<Mbti, Map<String, List<Mbti>>> createCompatibilityMap() {
		Map<Mbti, Map<String, List<Mbti>>> map = new HashMap<>();

		// ISTJ
		map.put(Mbti.ISTJ, Map.of(
			"찰떡궁합", List.of(Mbti.ESFP, Mbti.ESTP),
			"충돌잦은궁합", List.of(Mbti.ENFP, Mbti.INFP)
		));

		// ISFJ
		map.put(Mbti.ISFJ, Map.of(
			"찰떡궁합", List.of(Mbti.ESTP, Mbti.ESFP),
			"충돌잦은궁합", List.of(Mbti.ENTP, Mbti.INFP)
		));

		// INFJ
		map.put(Mbti.INFJ, Map.of(
			"찰떡궁합", List.of(Mbti.ENFP, Mbti.ENTP),
			"충돌잦은궁합", List.of(Mbti.ESTP, Mbti.ISTP)
		));

		// INTJ
		map.put(Mbti.INTJ, Map.of(
			"찰떡궁합", List.of(Mbti.ENFP, Mbti.ENTP),
			"충돌잦은궁합", List.of(Mbti.ESFP, Mbti.ISFP)
		));

		// ISTP
		map.put(Mbti.ISTP, Map.of(
			"찰떡궁합", List.of(Mbti.ISFJ, Mbti.ESFJ),
			"충돌잦은궁합", List.of(Mbti.ENFP, Mbti.ENFJ)
		));

		// ISFP
		map.put(Mbti.ISFP, Map.of(
			"찰떡궁합", List.of(Mbti.ISFJ, Mbti.ESFJ),
			"충돌잦은궁합", List.of(Mbti.ENTJ, Mbti.ENTP)
		));

		// INFP
		map.put(Mbti.INFP, Map.of(
			"찰떡궁합", List.of(Mbti.ENFJ, Mbti.INFJ),
			"충돌잦은궁합", List.of(Mbti.ESTJ, Mbti.ISTJ)
		));

		// INTP
		map.put(Mbti.INTP, Map.of(
			"찰떡궁합", List.of(Mbti.ENFJ, Mbti.ENTJ),
			"충돌잦은궁합", List.of(Mbti.ISFJ, Mbti.ESFJ)
		));

		// ESTP
		map.put(Mbti.ESTP, Map.of(
			"찰떡궁합", List.of(Mbti.ISFJ, Mbti.ISTJ),
			"충돌잦은궁합", List.of(Mbti.INFJ, Mbti.INFP)
		));

		// ESFP
		map.put(Mbti.ESFP, Map.of(
			"찰떡궁합", List.of(Mbti.ISFJ, Mbti.ISTJ),
			"충돌잦은궁합", List.of(Mbti.INTJ, Mbti.INFJ)
		));

		// ENFP
		map.put(Mbti.ENFP, Map.of(
			"찰떡궁합", List.of(Mbti.INFJ, Mbti.INTJ),
			"충돌잦은궁합", List.of(Mbti.ISTJ, Mbti.ESTJ)
		));

		// ENTP
		map.put(Mbti.ENTP, Map.of(
			"찰떡궁합", List.of(Mbti.INFJ, Mbti.INFP),
			"충돌잦은궁합", List.of(Mbti.ISTJ, Mbti.ISFJ)
		));

		// ESTJ
		map.put(Mbti.ESTJ, Map.of(
			"찰떡궁합", List.of(Mbti.ISFP, Mbti.ISTP),
			"충돌잦은궁합", List.of(Mbti.INFP, Mbti.ENFP)
		));

		// ESFJ
		map.put(Mbti.ESFJ, Map.of(
			"찰떡궁합", List.of(Mbti.ISFP, Mbti.INFP),
			"충돌잦은궁합", List.of(Mbti.INTJ, Mbti.INTP)
		));

		// ENFJ
		map.put(Mbti.ENFJ, Map.of(
			"찰떡궁합", List.of(Mbti.INFP, Mbti.INFJ),
			"충돌잦은궁합", List.of(Mbti.ISTP, Mbti.ESTP)
		));

		// ENTJ
		map.put(Mbti.ENTJ, Map.of(
			"찰떡궁합", List.of(Mbti.INTP, Mbti.INTJ),
			"충돌잦은궁합", List.of(Mbti.ISFP, Mbti.ESFP)
		));

		return map;
	}

	private void validateUser(User user) {
		if (!user.isOnboardingCompleted()) {
			throw new CustomException(ErrorCode.USER_ONBOARDING_NOT_COMPLETED);
		}
	}
}
