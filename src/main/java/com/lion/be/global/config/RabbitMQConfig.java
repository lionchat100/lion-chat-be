package com.lion.be.global.config;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    public static final String CHAT_QUEUE_NAME = "chat.queue"; // 실제로는 익명 큐를 사용하므로 이 이름은 참조용

    /**
     * 연결된 모든 큐에 메시지를 브로드캐스트
     */
    @Bean
    public FanoutExchange chatExchange() {
        return new FanoutExchange(CHAT_EXCHANGE_NAME);
    }

    /**
     * 애플리케이션 인스턴스마다 고유한 큐가 생성되고, 연결이 끊어지면 자동으로 삭제됨
     * 이는 여러 서버 인스턴스가 각자 큐를 가지고 동일한 Exchange에 바인딩되도록 함
     */
    @Bean
    public Queue anonymousQueue() {
        return new AnonymousQueue();
    }

    /**
     * 위에서 정의한 Exchange와 Queue를 연결
     */
    @Bean
    public Binding binding(Queue anonymousQueue, FanoutExchange chatExchange) {
        return BindingBuilder.bind(anonymousQueue).to(chatExchange);
    }

}
