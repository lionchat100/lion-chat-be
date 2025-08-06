package com.lion.be.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms // JMS 활성화
public class ActiveMQConfig {

    // 메시지 처리를 위한 Topic 이름 정의
    public static final String CHAT_TOPIC = "chat.topic";

    /**
     * Jackson 라이브러리를 사용해 메시지를 JSON으로 변환하는 MessageConverter를 설정합니다.
     * RabbitMQ 설정과 거의 동일합니다.
     * @return MessageConverter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT); // 메시지 타입을 TEXT로 설정
        converter.setTypeIdPropertyName("_type"); // 타입 정보 저장을 위한 프로퍼티 이름 설정

        // Java 8 날짜/시간 API 지원을 위한 ObjectMapper 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(objectMapper);

        return converter;
    }

    /**
     * @JmsListener가 사용할 컨테이너 팩토리를 설정합니다.
     * Pub/Sub 모델(Topic 방식)을 사용하기 위해 pubSubDomain을 true로 설정합니다.
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                          MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setPubSubDomain(true); // Topic 사용을 위해 true로 설정
        return factory;
    }

}