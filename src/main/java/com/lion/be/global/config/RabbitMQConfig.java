package com.lion.be.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    public static final String CHAT_QUEUE_NAME = "chat.queue";
    // 라우팅 키 패턴. "chat.message.123" 같은 형식의 키를 가진 메시지를 바인딩
    public static final String ROUTING_KEY_PATTERN = "chat.message.*";

    /**
     * TopicExchange: 라우팅 키를 기반으로 메시지를 큐에 전달하는 유연한 방식의 Exchange
     */
    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE_NAME, true, false);
    }

    /**
     * Queue: 서버가 재시작되어도 메시지가 유실되지 않도록 내구성 있는(durable) 큐를 생성
     */
    @Bean
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE_NAME, true);
    }

    /**
     * Binding: 위에서 정의한 Exchange와 Queue를 라우팅 키 패턴으로 연결
     */
    @Bean
    public Binding binding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue)
                .to(chatExchange)
                .with(ROUTING_KEY_PATTERN);
    }

    // ✨✨✨✨✨✨✨✨ 아래 내용을 추가합니다 ✨✨✨✨✨✨✨✨

    /**
     * RabbitMQ 메시지를 JSON으로 직렬화하고 역직렬화할 때 사용할 MessageConverter를 설정합니다.
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        // LocalDateTime, ZonedDateTime 등 Java 8 날짜/시간 API를 지원하도록 설정
        objectMapper.registerModule(new JavaTimeModule());
        // 날짜를 ISO-8601 형식의 문자열로 직렬화하도록 설정 (e.g., "2025-08-04T20:05:08.880")
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * RabbitTemplate을 커스터마이징하여 위에서 설정한 MessageConverter를 사용하도록 합니다.
     * 이 설정을 통해 RabbitTemplate.convertAndSend()가 객체를 JSON으로 변환하여 전송하게 됩니다.
     * @param connectionFactory Spring Boot가 자동으로 설정해주는 RabbitMQ 연결 팩토리
     * @param messageConverter 위에서 정의한 jsonMessageConverter Bean
     * @return 커스터마이징된 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    /**
     * @RabbitListener가 메시지를 처리할 때 사용할 컨테이너 팩토리를 설정합니다.
     * 여기서 MessageConverter를 설정해주어야 리스너가 JSON 메시지를 올바르게 DTO 객체로 변환할 수 있습니다.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // 필요에 따라 다른 설정 추가 가능 (e.g., 동시 소비자 수, prefetch count 등)
        return factory;
    }

}
