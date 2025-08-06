package com.lion.be.global.config;

import com.lion.be.global.interceptor.StompInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

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
        // 1. STOMP Relay가 Amazon MQ와 통신할 때 사용할 TCP 클라이언트를 설정합니다.
        ReactorNettyTcpClient<byte[]> tcpClient = new ReactorNettyTcpClient<>(client ->
                client.host(rabbitmqHost)          // application.yml에서 주입된 호스트
                        .port(rabbitmqPort)          // application.yml에서 주입된 SSL 포트 (61614)
                        .secure(),                   // <-- SSL/TLS 사용을 명시합니다.
                new StompReactorNettyCodec());

        // 2. StompBrokerRelay를 설정합니다.
        registry.setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic") // '/topic'으로 시작하는 destination을 외부 브로커가 처리
                .setClientLogin(rabbitmqClientName)
                .setClientPasscode(rabbitmqClientPassword)
                .setSystemLogin(rabbitmqSystemName)
                .setSystemPasscode(rabbitmqSystemPassword)
                .setSystemHeartbeatSendInterval(20000) // Heartbeat 간격을 늘려 안정성 확보
                .setSystemHeartbeatReceiveInterval(20000)
                .setTcpClient(tcpClient); // <-- 위에서 생성한 SSL 적용 TCP 클라이언트를 설정합니다.

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }


}