package com.lion.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile("!test") // "test" 프로필이 아닐 때만 이 설정을 사용
public class WebSocketProdConfig implements WebSocketMessageBrokerConfigurer {

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
        // SSL을 사용하는 TCP 클라이언트 설정
        ReactorNettyTcpClient<byte[]> tcpClient = new ReactorNettyTcpClient<>(client ->
                client.host(rabbitmqHost)
                        .port(rabbitmqPort)
                        .secure(), // SSL 활성화
                new StompReactorNettyCodec());

        registry.setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic")
                .setClientLogin(rabbitmqClientName)
                .setClientPasscode(rabbitmqClientPassword)
                .setSystemLogin(rabbitmqSystemName)
                .setSystemPasscode(rabbitmqSystemPassword)
                .setSystemHeartbeatSendInterval(20000)
                .setSystemHeartbeatReceiveInterval(20000)
                .setTcpClient(tcpClient); // SSL 클라이언트 주입
    }

}