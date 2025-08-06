package com.lion.be.global.config;

import com.lion.be.global.interceptor.StompInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.messaging.stomp.broker-relay.host}")
    private String relayHost;

    @Value("${spring.messaging.stomp.broker-relay.port}")
    private int relayPort;

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
        this.stompInterceptor = stompInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // ✨ [수정] ActiveMQ STOMP Relay 설정으로 변경
        registry.setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic")
                .setSystemHeartbeatSendInterval(10000)
                .setSystemHeartbeatReceiveInterval(10000)
                .setRelayHost(relayHost)        // 하드코딩된 값 대신 주입받은 변수 사용
                .setRelayPort(relayPort)        // 하드코딩된 값 대신 주입받은 변수 사용
                .setClientLogin(clientLogin)
                .setClientPasscode(clientPasscode)
                .setSystemLogin(systemLogin)
                .setSystemPasscode(systemPasscode);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }

}