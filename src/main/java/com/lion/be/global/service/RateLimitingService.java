package com.lion.be.global.service;

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

    private final Map<String, Bucket> feedCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> feedCommentCache = new ConcurrentHashMap<>();

    /**
     * 피드 생성용 버킷을 조회/생성합니다.
     * Key: "feed-{userId}"
     */
    public Bucket resolveFeedBucket(Long userId) {
        String key = "feed-" + userId;
        return feedCache.computeIfAbsent(key, k -> createNewFeedBucket());
    }

    /**
     * 피드 댓글 생성용 버킷을 조회/생성합니다.
     * Key: "comment-{feedId}-{userId}"
     */
    public Bucket resolveFeedCommentBucket(Long feedId, Long userId) {
        String key = "comment-" + feedId + "-" + userId;
        return feedCommentCache.computeIfAbsent(key, k -> createNewFeedCommentBucket());
    }

    /**
     * 피드 생성 정책에 맞는 버킷을 생성합니다.
     * (3초에 1개, 10분에 5개)
     */
    private Bucket createNewFeedBucket() {
        Bandwidth limitPer3Seconds = Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(3)));
        Bandwidth limitPer10Minutes = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)));
        return Bucket4j.builder()
                .addLimit(limitPer3Seconds)
                .addLimit(limitPer10Minutes)
                .build();
    }

    /**
     * 피드 댓글 생성 정책에 맞는 버킷을 생성합니다.
     * (3초에 1개, 1분에 5개)
     */
    private Bucket createNewFeedCommentBucket() {
        Bandwidth limitPer3Seconds = Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(3)));
        Bandwidth limitPer1Minute = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limitPer3Seconds)
                .addLimit(limitPer1Minute)
                .build();
    }

    /**
     * 테스트용: 모든 버킷 캐시를 비웁니다.
     */
    public void clearBuckets() {
        feedCache.clear();
        feedCommentCache.clear();
    }

}