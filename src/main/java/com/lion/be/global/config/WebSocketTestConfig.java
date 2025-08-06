package com.lion.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile("test") // "test" 프로필일 때만 이 설정을 사용
public class WebSocketTestConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.messaging.stomp.broker-relay.host}")
    private String rabbitmqHost;

    @Value("${spring.messaging.stomp.broker-relay.port}")
    private int rabbitmqPort;

    @Value("${spring.messaging.stomp.broker-relay.client-login}")
    private String rabbitmqClientName;

    @Value("${spring.messaging.stomp.broker-relay.client-passcode}")
    private String rabbitmqClientPassword;

    @Value("${spring.messaging.stomp.broker-relay.system-login}")
    private String rabbitmqSystemName;

    @Value("${spring.messaging.stomp.broker-relay.system-passcode}")
    private String rabbitmqSystemPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // SSL 설정이 없는 기본 StompBrokerRelay 설정
        registry.setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic")
                .setRelayHost(rabbitmqHost)
                .setRelayPort(rabbitmqPort)
                .setClientLogin(rabbitmqClientName)
                .setClientPasscode(rabbitmqClientPassword)
                .setSystemLogin(rabbitmqSystemName)
                .setSystemPasscode(rabbitmqSystemPassword);
    }

}