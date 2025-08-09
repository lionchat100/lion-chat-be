package com.lion.be.acceptance.chat;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.repository.MessageEntityAdapter;
import com.lion.be.chat.service.MessagePersistence;
import com.lion.be.chat.service.RabbitMessagePublisher;
import com.lion.be.global.config.RabbitMQConfig;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RabbitMessagePublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private MessagePersistence messagePersistence;
    @InjectMocks
    private RabbitMessagePublisher rabbitMessagePublisher;

    private final MessageEntityAdapter adapter = new MessageEntityAdapter();

    @BeforeEach
    void setUp() {
        rabbitMessagePublisher = new RabbitMessagePublisher(rabbitTemplate, adapter, messagePersistence);
        rabbitMessagePublisher.setUpCallbacks();
    }

    @Test
    void publishMessage_should_send_to_rabbitmq() {
        // Given
        ChatMessage message = new ChatMessage(
                new ObjectId(),
                1L,
                "userA",
                1L,
                ZonedDateTime.now().toInstant(),
                "메시지 발행 테스트",
                false,
                MessageStatus.SENT
        );
        String expectedRoutingKey = "chat.message." + 1L;

        // When
        rabbitMessagePublisher.publishMessage(message);

        // Then
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.CHAT_EXCHANGE_NAME),
                eq(expectedRoutingKey),
                any(ChatMessageResponse.class)
        );
    }

    @Test
    void setupCallbacks_should_set_confirm_callback() {
        // Then
        verify(rabbitTemplate, times(1)).setConfirmCallback(any(ConfirmCallback.class));
    }

    @Test
    void setupCallbacks_should_set_returns_callback_and_handle_failure() {
        // Given
        MessageConverter converter = new Jackson2JsonMessageConverter();
        when(rabbitTemplate.getMessageConverter()).thenReturn(converter);

        ArgumentCaptor<RabbitTemplate.ReturnsCallback> callbackCaptor = ArgumentCaptor.forClass(RabbitTemplate.ReturnsCallback.class);

        ObjectId expectedObjectId = new ObjectId();
        verify(rabbitTemplate, times(1)).setReturnsCallback(callbackCaptor.capture());
        RabbitTemplate.ReturnsCallback capturedCallback = callbackCaptor.getValue();

        ChatMessageResponse response = new ChatMessageResponse(
                expectedObjectId.toString(),
                1L,
                "cinnamein",
                1L,
                ZonedDateTime.now(),
                "failed message"
        );

        MessageProperties properties = new MessageProperties();
        Message returnedMessage = converter.toMessage(response, properties);

        ReturnedMessage returned = new ReturnedMessage(
                returnedMessage,
                404,
                "NOT_FOUND",
                "chat.exchange",
                "chat.message.1"
        );

        // When
        capturedCallback.returnedMessage(returned);

        // Then
        verify(messagePersistence, times(1))
                .updateMessageStatus(expectedObjectId, MessageStatus.PENDING);
    }
}
