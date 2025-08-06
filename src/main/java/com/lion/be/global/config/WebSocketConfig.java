package com.lion.be.global.config;

import com.lion.be.global.interceptor.StompInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

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

    private final StompInterceptor stompInterceptor;

    public WebSocketConfig(StompInterceptor stompInterceptor) {
        // 기본 생성자
        this.stompInterceptor = stompInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket 연결을 시작할 엔드포인트
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/topic"으로 시작하는 목적지를 가진 메시지를 브로커로 라우팅
        // 클라이언트는 이 경로를 구독하여 메시지를 수신
        //registry.enableSimpleBroker("/topic");

        // "/app"으로 시작하는 목적지를 가진 메시지를 @MessageMapping 메서드로 라우팅
        // 클라이언트가 서버로 메시지를 보낼 때 사용하는 경로
        //registry.setApplicationDestinationPrefixes("/app");

        // StompBrokerRelay를 사용하도록 설정
        registry.setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic") // '/topic'으로 시작하는 destination을 외부 브로커가 처리
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

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("stomp-heartbeat-thread-");

        return scheduler;
    }



}