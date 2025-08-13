package com.lion.be.global.config; // 적절한 config 패키지

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitingConfig {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, byte[]> connection;

    @Value("${spring.data.redis.host}")
    private String redisUrl;

    @PostConstruct
    public void init() {
        this.redisClient = RedisClient.create(redisUrl);
        // StringCodec은 키에, ByteArrayCodec은 값(버킷 상태)에 사용됩니다.
        this.connection = redisClient.connect(new io.lettuce.core.codec.RedisCodec<String, byte[]>() {
            @Override
            public java.nio.ByteBuffer encodeKey(String key) {
                return StringCodec.UTF8.encodeKey(key);
            }

            @Override
            public String decodeKey(java.nio.ByteBuffer bytes) {
                return StringCodec.UTF8.decodeKey(bytes);
            }

            @Override
            public java.nio.ByteBuffer encodeValue(byte[] value) {
                return ByteArrayCodec.INSTANCE.encodeValue(value);
            }

            @Override
            public byte[] decodeValue(java.nio.ByteBuffer bytes) {
                return ByteArrayCodec.INSTANCE.decodeValue(bytes);
            }
        });
    }

    @Bean
    public ProxyManager<String> proxyManager() {
        // Lettuce와 Bucket4j를 연결하는 ProxyManager를 생성합니다.
        return LettuceBasedProxyManager.builderFor(connection).build();
    }

    @PreDestroy
    public void destroy() {
        if (connection != null) {
            connection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }

}