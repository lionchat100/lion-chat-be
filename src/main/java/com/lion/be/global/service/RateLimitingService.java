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

    public Bucket resolveFeedBucketPerSecond(Long userId) {
        String key = "rate-limit:feed:second:" + userId;
        return proxyManager.builder().build(key, this::createFeedBucketConfigPerSecond);
    }

    public Bucket resolveFeedBucketPerMinute(Long userId) {
        String key = "rate-limit:feed:minute:" + userId;
        return proxyManager.builder().build(key, this::createFeedBucketConfigPerMinute);
    }

    public Bucket resolveFeedCommentBucketPerSecond(Long feedId, Long userId) {
        String key = "rate-limit:comment:second:" + feedId + ":" + userId;
        return proxyManager.builder().build(key, this::createFeedCommentBucketConfigPerSecond);
    }

    public Bucket resolveFeedCommentBucketPerMinute(Long feedId, Long userId) {
        String key = "rate-limit:comment:minute:" + feedId + ":" + userId;
        return proxyManager.builder().build(key, this::createFeedCommentBucketConfigPerMinute);
    }

    public Bucket resolveChatBucketBurst(Long chatRoomId, Long userId) {
        String key = "rate-limit:chat:burst:" + chatRoomId + ":" + userId;
        return proxyManager.builder().build(key, this::createChatBucketConfigBurst);
    }

    public Bucket resolveChatBucketSustained(Long chatRoomId, Long userId) {
        String key = "rate-limit:chat:sustained:" + chatRoomId + ":" + userId;
        return proxyManager.builder().build(key, this::createChatBucketConfigSustained);
    }

    private BucketConfiguration createFeedBucketConfigPerSecond() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(3))))
                .build();
    }

    private BucketConfiguration createFeedBucketConfigPerMinute() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10))))
                .build();
    }

    private BucketConfiguration createFeedCommentBucketConfigPerSecond() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(3))))
                .build();
    }

    private BucketConfiguration createFeedCommentBucketConfigPerMinute() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build();
    }

    private BucketConfiguration createChatBucketConfigBurst() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(10)))) // 장기
                .build();
    }

    private BucketConfiguration createChatBucketConfigSustained() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(2, Refill.intervally(2, Duration.ofSeconds(1)))) // 단기
                .build();
    }

    @Profile("test")
    public void clearAllBuckets() {
        Set<String> keys = redisTemplate.keys("rate-limit:*");
        if (keys != null && !keys.isEmpty()) {
            log.info("[TEST] Deleting {} rate-limiting buckets from Redis.", keys.size());
            redisTemplate.delete(keys);
        } else {
            log.info("[TEST] No rate-limiting buckets to delete from Redis.");
        }
    }

}