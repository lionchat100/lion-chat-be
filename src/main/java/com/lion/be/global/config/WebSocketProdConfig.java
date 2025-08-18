package com.lion.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile("!test") // "test" 프로필이 아닐 때만 이 설정을 사용 (운영/개발 환경)
public class WebSocketProdConfig implements WebSocketMessageBrokerConfigurer {

    // application.yml에 정의된 RabbitMQ 접속 정보
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    // 포트는 STOMP 기본 포트인 61613을 사용합니다.
    private static final int STOMP_PORT = 61613;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 이 부분이 스케일 아웃을 가능하게 하는 핵심 설정입니다.
        registry.setApplicationDestinationPrefixes("/app"); // 메시지 발행 prefix
        registry.enableStompBrokerRelay("/topic", "/queue") // 구독 prefix. 외부 브로커(RabbitMQ) 사용 설정
                .setRelayHost(host)
                .setRelayPort(STOMP_PORT)
                .setClientLogin(username)
                .setClientPasscode(password)
                .setSystemLogin(username)
                .setSystemPasscode(password);
    }

}