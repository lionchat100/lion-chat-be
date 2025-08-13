package com.lion.be.global.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitingService {

    private final ProxyManager<String> proxyManager;
    private final RedisTemplate<String, Object> redisTemplate;

    public Bucket resolveFeedBucket(Long userId) {
        String key = "rate-limit:feed:" + userId;
        return proxyManager.builder().build(key, this::createNewFeedBucketConfig);
    }

    public Bucket resolveFeedCommentBucket(Long feedId, Long userId) {
        String key = "rate-limit:comment:" + feedId + ":" + userId;
        return proxyManager.builder().build(key, this::createNewFeedCommentBucketConfig); // 메소드명 변경
    }

    public Bucket resolveChatBucket(Long chatRoomId, Long userId) {
        String key = "rate-limit:chat:" + chatRoomId + ":" + userId;
        return proxyManager.builder().build(key, this::createNewChatBucketConfig);
    }

    private BucketConfiguration createNewFeedBucketConfig() {
        Bandwidth limitPer3Seconds = Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(3)));
        Bandwidth limitPer10Minutes = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)));
        return BucketConfiguration.builder()
                .addLimit(limitPer3Seconds)
                .addLimit(limitPer10Minutes)
                .build();
    }

    private BucketConfiguration createNewFeedCommentBucketConfig() {
        Bandwidth limitPer3Seconds = Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(3)));
        Bandwidth limitPer1Minute = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return BucketConfiguration.builder()
                .addLimit(limitPer3Seconds)
                .addLimit(limitPer1Minute)
                .build();
    }

    private BucketConfiguration createNewChatBucketConfig() {
        // 정책: 1초에 최대 2개, 10초간 총 10개까지 허용
        Bandwidth burstLimit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(10)));
        Bandwidth sustainedLimit = Bandwidth.classic(2, Refill.intervally(2, Duration.ofSeconds(1)));

        return BucketConfiguration.builder()
                .addLimit(burstLimit)       // 장기적인 제한
                .addLimit(sustainedLimit)   // 단기적인 제한
                .build();
    }

    @Profile("test")
    public void clearAllBuckets() {
        // "rate-limit:" 패턴으로 시작하는 모든 키를 찾습니다.
        Set<String> keys = redisTemplate.keys("rate-limit:*");
        if (keys != null && !keys.isEmpty()) {
            log.info("[TEST] Deleting {} rate-limiting buckets from Redis.", keys.size());
            redisTemplate.delete(keys);
        } else {
            log.info("[TEST] No rate-limiting buckets to delete from Redis.");
        }
    }

}