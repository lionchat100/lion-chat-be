package com.lion.be.global.service; // 혹은 적절한 패키지 경로

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RateLimitingService {

    // 사용자 ID(Long)를 키로 사용하여 버킷을 저장합니다.
    private final Map<Long, Bucket> cache = new ConcurrentHashMap<>();

    // 사용자 ID로 버킷을 조회하거나 새로 생성합니다.
    public Bucket resolveBucket(Long userId) {
        return cache.computeIfAbsent(userId, this::createNewBucket);
    }

    private Bucket createNewBucket(Long userId) {
        // 3초에 1개의 토큰을 리필하는 대역폭 (게시물 1개 작성 제한)
        Bandwidth limitPer3Seconds = Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(3)));
        // 10분에 5개의 토큰을 리필하는 대역폭 (게시물 5개 작성 제한)
        Bandwidth limitPer10Minutes = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)));

        return Bucket4j.builder()
                .addLimit(limitPer3Seconds)
                .addLimit(limitPer10Minutes)
                .build();
    }

    /**
     * 테스트 코드에서만 사용하기 위한 메소드.
     * 모든 사용자의 버킷 정보를 초기화합니다.
     */
    public void clearBuckets() {
        cache.clear();
    }

}