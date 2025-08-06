package com.lion.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile("local") // "local" 프로필일 때만 이 설정을 사용
public class WebSocketLocalConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.messaging.stomp.broker-relay.host}")
    private String brokerHost;

    @Value("${spring.messaging.stomp.broker-relay.port}")
    private int brokerPort;

    @Value("${spring.messaging.stomp.broker-relay.client-login}")
    private String clientLogin;

    @Value("${spring.messaging.stomp.broker-relay.client-passcode}")
    private String clientPasscode;

    @Value("${spring.messaging.stomp.broker-relay.system-login}")
    private String systemLogin;

    @Value("${spring.messaging.stomp.broker-relay.system-passcode}")
    private String systemPasscode;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // SSL 설정이 없는 기본 StompBrokerRelay 설정
        registry.setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic")
                .setSystemHeartbeatSendInterval(10000)
                .setSystemHeartbeatReceiveInterval(10000)
                .setTaskScheduler(taskScheduler())
                .setRelayHost(brokerHost)
                .setRelayPort(brokerPort)
                .setClientLogin(clientLogin)
                .setClientPasscode(clientPasscode)
                .setSystemLogin(systemLogin)
                .setSystemPasscode(systemPasscode);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("stomp-heartbeat-thread-");

        return scheduler;
    }

}