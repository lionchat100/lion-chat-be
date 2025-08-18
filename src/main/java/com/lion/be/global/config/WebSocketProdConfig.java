package com.lion.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile("!test")
public class WebSocketProdConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${mq.stomp.host}")
    private String host;

    @Value("${mq.stomp.port}")
    private int port;

    @Value("${mq.stomp.username}")
    private String username;

    @Value("${mq.stomp.password}")
    private String password;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. Application Destination Prefix 설정 (클라이언트 -> 서버)
        registry.setApplicationDestinationPrefixes("/app");

        // 2. STOMP Broker Relay 설정 (서버 <-> 외부 브로커 <-> 클라이언트)
        registry.enableStompBrokerRelay("/topic", "/queue") // ActiveMQ의 Destination Prefix
                .setRelayHost(host)
                .setRelayPort(port)
                .setSystemLogin(username)
                .setSystemPasscode(password)
                .setClientLogin(username)
                .setClientPasscode(password);
    }

}