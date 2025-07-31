package com.lion.be.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket 연결을 시작할 엔드포인트
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/topic"으로 시작하는 목적지를 가진 메시지를 브로커로 라우팅
        // 클라이언트는 이 경로를 구독하여 메시지를 수신
        registry.enableSimpleBroker("/topic");

        // "/app"으로 시작하는 목적지를 가진 메시지를 @MessageMapping 메서드로 라우팅
        // 클라이언트가 서버로 메시지를 보낼 때 사용하는 경로
        registry.setApplicationDestinationPrefixes("/app");
    }

}