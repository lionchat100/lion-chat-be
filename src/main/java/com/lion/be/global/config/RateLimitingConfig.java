package com.lion.be.global.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitingConfig {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, byte[]> connection;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:#{null}}")
    private String redisPassword;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean sslEnabled;

    @PostConstruct
    public void init() {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(redisHost)
                .withPort(redisPort)
                .withSsl(sslEnabled);

        if (redisPassword != null && !redisPassword.isBlank()) {
            builder.withPassword(redisPassword.toCharArray());
        }

        RedisURI redisUri = builder.build();

        this.redisClient = RedisClient.create(redisUri);

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
        return LettuceBasedProxyManager.builderFor(connection)
                .withExpirationStrategy(ExpirationAfterWriteStrategy.fixedTimeToLive(Duration.ofMinutes(15)))
                .build();
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