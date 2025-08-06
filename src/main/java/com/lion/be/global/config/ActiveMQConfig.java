package com.lion.be.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class ActiveMQConfig {

    // ActiveMQ 브로커 URL (필요시 application.properties 등에서 관리)
    @Value("${spring.activemq.broker-url}")
    private String BROKER_URL;

    @Value("${spring.jms.client-id}")
    private String clientId;

    // ### 1. 재시도 및 DLQ 정책 설정 (RabbitMQ의 RetryInterceptor 역할) ###
    /**
     * 메시지 재전송 정책(Redelivery Policy)을 설정합니다.
     * RabbitMQ의 RetryInterceptor와 유사한 기능을 수행합니다.
     * @return RedeliveryPolicy
     */
    @Bean
    public RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        // RabbitMQ의 maxAttempts(3)는 '최초 1회 + 재시도 2회'를 의미하므로,
        // ActiveMQ의 maximumRedeliveries는 2로 설정합니다.
        redeliveryPolicy.setMaximumRedeliveries(2); // 최대 재시도 횟수

        // RabbitMQ의 backOffOptions(1000, 2.0, 5000)와 유사하게 설정
        redeliveryPolicy.setInitialRedeliveryDelay(1000L); // 초기 재시도 지연 시간 (ms)
        redeliveryPolicy.setUseExponentialBackOff(true);   // 백오프 적용
        redeliveryPolicy.setBackOffMultiplier(2.0);        // 지연 시간 증가 배수
        redeliveryPolicy.setMaximumRedeliveryDelay(5000L); // 최대 재시도 지연 시간

        // 재시도 횟수 초과 시, 메시지는 자동으로 ActiveMQ의 Dead-Letter Queue로 이동합니다.
        // (기본적으로 'ActiveMQ.DLQ'라는 이름의 큐)
        return redeliveryPolicy;
    }

    // ### 2. ConnectionFactory 설정 (재시도 정책 포함) ###
    /**
     * ActiveMQ에 실제 연결을 수행하는 ConnectionFactory를 생성하고, 재시도 정책을 적용합니다.
     * @param redeliveryPolicy 위에서 정의한 재전송 정책
     * @return ActiveMQConnectionFactory
     */
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(RedeliveryPolicy redeliveryPolicy) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(BROKER_URL);
        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy); // 재시도 정책 주입
        activeMQConnectionFactory.setClientID(clientId);
        return activeMQConnectionFactory;
    }

    /**
     * 성능 향상을 위해 연결을 캐싱하는 CachingConnectionFactory를 생성합니다.
     * 여러 JMS 컴포넌트에 이 빈이 주입됩니다.
     * @Primary: 여러 ConnectionFactory 중 이 빈을 기본으로 사용하도록 지정합니다.
     */
    @Bean
    @Primary // @Qualifier를 생략할 수 있도록 기본 빈으로 지정
    public ConnectionFactory cachingConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        return new CachingConnectionFactory(activeMQConnectionFactory);
    }

    // ### 3. JSON 메시지 컨버터 설정 ###
    /**
     * JMS 메시지를 JSON으로 변환하는 MessageConverter를 설정합니다.
     * RabbitMQ의 Jackson2JsonMessageConverter와 동일한 역할을 합니다.
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(objectMapper);

        return converter;
    }

    // ### 4. JmsTemplate 및 Listener Factory 설정 ###
    /**
     * 큐(Queue) 메시지 리스너를 위한 컨테이너 팩토리입니다.
     */
    @Bean("jmsQueueListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // 큐는 기본적으로 Pub/Sub 도메인이 아님 (false가 기본값)
        // factory.setPubSubDomain(false);
        return factory;
    }

    /**
     * 토픽(Topic) 메시지 리스너를 위한 컨테이너 팩토리입니다.
     * 영속 구독(durable subscription)을 사용하도록 설정합니다.
     */
    @Bean("jmsTopicListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsTopicListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        // Topic 리스너를 위한 핵심 설정
        factory.setPubSubDomain(true);
        factory.setSubscriptionDurable(true);

        // 영속 구독을 사용하려면 Client ID가 필수입니다.
        // application.properties에서 'spring.jms.client-id=고유ID' 형식으로 설정하는 것을 권장합니다.
        return factory;
    }

    /**
     * 메시지 전송에 사용될 JmsTemplate을 설정합니다.
     */
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }
}
