package com.lion.be.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMQConfig {

    public static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    public static final String CHAT_QUEUE_NAME = "chat.queue";
    // 라우팅 키 패턴. "chat.message.123" 같은 형식의 키를 가진 메시지를 바인딩
    public static final String ROUTING_KEY_PATTERN = "chat.message.*";

    // DLQ 설정 추가
    public static final String DEAD_LETTER_EXCHANGE_NAME = "dead.letter.exchange";
    public static final String DEAD_LETTER_QUEUE_NAME = "dead.letter.chat.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "dead.letter.chat.message";

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
        return QueueBuilder.durable(CHAT_QUEUE_NAME)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE_NAME) // DLQ 설정
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY) // DLQ 라우팅 키 설정
                .build();
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

    /**
     * Dead Letter Exchange (DLX)를 생성합니다.
     * 일반 Topic Exchange와 동일하지만, 처리 실패한 메시지를 받는 용도로 사용됩니다.
     */
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE_NAME, true, false);
    }

    /**
     * Dead Letter Queue (DLQ)를 생성합니다.
     */
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE_NAME, true);
    }

    /**
     * Dead Letter Exchange와 Dead Letter Queue를 바인딩합니다.
     */
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DEAD_LETTER_ROUTING_KEY); // 특정 라우팅 키로 바인딩
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

        // ⭐ 이 부분을 활성화해야 합니다!
        factory.setAdviceChain(retryInterceptor()); // 재시도 정책 설정

        factory.setDefaultRequeueRejected(false);

        // 필요에 따라 다른 설정 추가 가능 (e.g., 동시 소비자 수, prefetch count 등)
        return factory;
    }

    //재시도 정책을 결정
    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3) // 최대 3번 재시도
                .backOffOptions(1000, 2.0, 5000) // 초기 1초, 2배씩 증가, 최대 5초
                .recoverer(new RejectAndDontRequeueRecoverer())// 재시도 후 실패 시 버림 (재큐 안함)
                .build();
    }

}
