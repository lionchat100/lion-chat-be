package com.lion.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile("prod") // "test" 프로필이 아닐 때만 이 설정을 사용
public class WebSocketProdConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Amazon MQ for RabbitMQ는 STOMP를 직접 지원하지 않으므로,
        // 스프링의 내장 SimpleBroker를 사용하도록 변경합니다.
        // 이 코드는 클라이언트와 서버 간의 STOMP 통신을 처리합니다.
        // 서버와 RabbitMQ 간의 통신은 RabbitTemplate과 @RabbitListener가 AMQP로 담당합니다.

        // "/topic", "/queue"를 목적지로 하는 메시지를 SimpleBroker가 처리
        registry.enableSimpleBroker("/topic", "/queue");

        // 클라이언트가 메시지를 보낼 때 사용할 접두사
        registry.setApplicationDestinationPrefixes("/app");
    }

}