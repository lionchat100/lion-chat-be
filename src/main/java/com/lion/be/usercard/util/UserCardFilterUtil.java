package com.lion.be.usercard.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.PreferenceType;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.UserVectorizer;
import com.lion.be.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

@Slf4j
@Component
public class UserCardFilterUtil {

	private final UserVectorizer userVectorizer;
	private final UserRepository userRepository;

	public UserCardFilterUtil(UserVectorizer userVectorizer, UserRepository userRepository) {
		this.userVectorizer = userVectorizer;
		this.userRepository = userRepository;
	}

	/**
	 * 저장된 클러스터 정보를 활용한 사용자 추천
	 * 1. 동일 클러스터 내 사용자 우선 추천
	 * 2. 부족하면 랜덤으로 보완
	 */
	public List<User> getRecommendedUsers(Long userId, int size, List<Long> excludeUserIds) {
		if (size <= 0) {
			log.warn("잘못된 size 값: {}", size);
			return new ArrayList<>();
		}

		User targetUser = userRepository.fetchById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 1. 동일 클러스터 사용자 조회
		List<User> clusterBasedUsers = new ArrayList<>(getClusterBasedRecommendations(targetUser, excludeUserIds, size));

		log.debug("클러스터 {} 기반 추천 결과: {}명", targetUser.getClusterId(), clusterBasedUsers.size());

		// 2. 클러스터 기반 결과가 충분하지 않으면 랜덤으로 보완
		if (clusterBasedUsers.size() < size) {
			int remainingSize = size - clusterBasedUsers.size();


			// 이미 추천된 사용자들도 제외 목록에 추가
			List<Long> extendedExcludeIds = new ArrayList<>(excludeUserIds != null ? excludeUserIds : List.of());
			clusterBasedUsers.forEach(user -> extendedExcludeIds.add(user.getId()));

			List<User> randomUsers = userRepository.fetchRandomUsersExcluding(
				userId, remainingSize, extendedExcludeIds);

			log.debug("랜덤 보완 추천 결과: {}명", randomUsers.size());

			clusterBasedUsers.addAll(randomUsers); //이제 안전하게 추가 가능
		}

		return clusterBasedUsers;
	}

	/**
	 * 저장된 클러스터 ID를 활용한 동일 클러스터 사용자 추천
	 */
	private List<User> getClusterBasedRecommendations(User targetUser, List<Long> excludeUserIds, int size) {
		Integer targetClusterId = targetUser.getClusterId();

		if (targetClusterId == null) {
			log.warn("사용자 {}의 클러스터 정보 없음", targetUser.getId());
			return new ArrayList<>(); // 빈 ArrayList 반환 (수정 가능한 리스트)
		}

		if (size <= 0) {
			log.warn("잘못된 size 값: {}", size);
			return new ArrayList<>(); // 빈 ArrayList 반환
		}

		// 동일 클러스터 사용자들 조회 (Repository에 메서드 추가 필요)
		List<User> sameClusterUsers = userRepository.fetchUsersByClusterExcluding(
			targetClusterId, targetUser.getId(), excludeUserIds, size);

		// 빈 리스트인 경우 빈 ArrayList 반환
		if (sameClusterUsers.isEmpty()) {
			return new ArrayList<>();
		}

		// 유사도 기반 정렬
		return sameClusterUsers.stream()
			.map(user -> new UserSimilarity(user, calculateSimilarity(targetUser, user)))
			.sorted((a, b) -> Double.compare(b.similarity, a.similarity))
			.limit(size)
			.map(us -> us.user)
			.toList();
	}

	/**
	 * 두 사용자 간 유사도 계산
	 * 이로직에서 비율을 조절하시면 됩니다. // 1번 선택시 7대 3 2번 선택시 3
	 */
	private double calculateSimilarity(User user1, User user2) {
		double mbtiCompatibility = userVectorizer.calculateMbtiCompatibility(user1, user2);
		double positionSimilarity = calculatePositionSimilarity(user1, user2);

		PreferenceType preference = user1.getPreferenceType();

		if (preference == PreferenceType.MBTI_FOCUSED) {
			return 0.7 * mbtiCompatibility + 0.3 * positionSimilarity;
		} else if (preference == PreferenceType.POSITION_FOCUSED) {
			return 0.3 * mbtiCompatibility + 0.7 * positionSimilarity;
		} else {
			// 기본값 (null인 경우나 기타) 현재는 Null 값 없음
			return 0.5 * mbtiCompatibility + 0.5 * positionSimilarity;
		}
	}

	/**
	 * 두 사용자의 포지션 유사도 계산
	 *
	 * 기존 방식: 같은 포지션이면 1.0, 다르면 0.0 (이분법적)
	 * 개선 방식: 포지션 간 유사도를 반영한 연속적 점수 계산
	 *
	 * 예시:
	 * - BACKEND vs BACKEND: 1.0 (동일)
	 * - BACKEND vs FULLSTACK: 0.8 (높은 유사도)
	 * - BACKEND vs PM: 0.2 (낮은 유사도)
	 *
	 * 계산 방법: 두 포지션 벡터의 내적(dot product)
	 * - 벡터는 getPositionVector()에서 각 포지션별 유사도 매트릭스로 정의됨
	 * - 내적 결과가 클수록 유사한 포지션임을 의미
	 *
	 * @param user1 첫 번째 사용자
	 * @param user2 두 번째 사용자
	 * @return 포지션 유사도 점수 (0.0 ~ 1.0+ 범위)
	 */
	private double calculatePositionSimilarity(User user1, User user2) {
		double[] vector1 = userVectorizer.getPositionVector(user1.getPosition());
		double[] vector2 = userVectorizer.getPositionVector(user2.getPosition());

		// 두 벡터의 내적 계산 (유사도)
		double similarity = 0.0;
		for (int i = 0; i < vector1.length; i++) {
			similarity += vector1[i] * vector2[i];
		}

		return similarity;
	}

	private record UserSimilarity(User user, double similarity) {}

	/**
	 * 신규 사용자를 기존 클러스터에 배정
	 * 전체 재클러스터링으로 정확한 클러스터 배정
	 */
	public Integer assignNewUserToCluster(User newUser) {
		try {
			List<User> allUsers = userRepository.fetchAllCompletedUsersExcluding(null, null);
			allUsers.add(newUser);

			if (allUsers.size() < 2) {
				log.warn("클러스터 배정 불가: 사용자 수 부족");
				return 0;
			}

			Map<User, Integer> clusterMap = performFullClustering(allUsers);
			Integer assignedCluster = clusterMap.get(newUser);

			log.info("신규 사용자 ID: {} 클러스터 {} 배정", newUser.getId(), assignedCluster);
			return assignedCluster != null ? assignedCluster : 0;

		} catch (Exception e) {
			log.error("신규 사용자 클러스터 배정 중 오류", e);
			return 0;
		}
	}

	/**
	 * K-means 클러스터링 수행 (신규 사용자 배정 시에만 사용)
	 */
	private Map<User, Integer> performFullClustering(List<User> users) {
		try {
			ArrayList<Attribute> attributes = new ArrayList<>();
			for(int i = 0; i < 9; i++) {
				attributes.add(new Attribute("attr" + i));
			}

			Instances dataset = new Instances("users", attributes, users.size());

			for(User user : users) {
				double[] vector = userVectorizer.vectorize(user);
				dataset.add(new DenseInstance(1.0, vector));
			}

			SimpleKMeans kmeans = new SimpleKMeans();
			int numClusters = Math.min(Math.max(2, users.size() / 5), 15);
			kmeans.setNumClusters(numClusters);
			kmeans.setMaxIterations(500);
			kmeans.setSeed(10);
			kmeans.buildClusterer(dataset);

			int[] assignments = kmeans.getAssignments();
			Map<User, Integer> result = new HashMap<>();
			for(int i = 0; i < users.size(); i++) {
				result.put(users.get(i), assignments[i]);
			}

			log.debug("클러스터링 완료: {}명을 {}개 클러스터로 분류", users.size(), numClusters);
			return result;

		} catch (Exception e) {
			throw new CustomException(ErrorCode.CLUSTERING_ERROR, e);
		}
	}
}
