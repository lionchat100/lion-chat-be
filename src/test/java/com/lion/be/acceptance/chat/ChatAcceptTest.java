package com.lion.be.acceptance.chat;

import com.lion.be.chat.controller.ChatController;
import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.dto.ChatRoomCreationResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.repository.ChatRoomRepository;
import com.lion.be.chat.repository.MessageEntityAdapter;
import com.lion.be.chat.service.AsyncChatRoomService;
import com.lion.be.chat.service.MessagePersistence;
import com.lion.be.chat.service.MessagePublisher;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ChatAcceptTest는 ChatController에 대한 단위 테스트 또는 통합 테스트를 Mockito를 활용하여 수행하는 클래스입니다.
 * RestAssured를 직접 사용하는 것이 아니라, 컨트롤러 메서드를 직접 호출하여 로직을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("채팅 컨트롤러 기능 테스트")
class ChatAcceptTest {

    @InjectMocks
    private ChatController chatController;

    @Mock
    private AsyncChatRoomService asyncChatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MessagePersistence messagePersistence;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private MessageEntityAdapter adapter;

    private ChatMessageRequest mockRequest;
    private ChatMessage mockMessage;
    private ChatMessageResponse mockResponse;

    @BeforeEach
    void setUp() {
        String objectIdString = "60c72b2f6b8c9b2f6b8c9b2f";
        // 테스트에 사용할 공통 Mock 객체 설정
        mockRequest = new ChatMessageRequest(
                1L,
                "cinnamein",
                2L,
                3L,
                "testing"
        );
        mockMessage = new ChatMessage(
                new ObjectId(objectIdString),
                2L,
                "cinnamein",
                1L,
                Instant.now(),
                null,
                null,
                MessageStatus.SENT
        );
        mockResponse = new ChatMessageResponse(
                objectIdString,
                1L,
                "cinnamein",
                2L,
                ZonedDateTime.now(),
                "testing"
        );

        when(adapter.fromRequest(any(ChatMessageRequest.class))).thenReturn(mockMessage);
        when(messagePersistence.saveMessage(any(ChatMessage.class))).thenReturn(mockMessage);
        when(adapter.toResponse(any(ChatMessage.class))).thenReturn(mockResponse);
    }

    @Nested
    @DisplayName("메시지 전송")
    class SendMessageTest {

        @DisplayName("채팅방이 이미 존재하는 경우 메시지 전송에 성공한다.")
        @Test
        void whenChatRoomExists_thenSendMessageSuccess() {
            // Given
            when(chatRoomRepository.existsById(anyLong())).thenReturn(true);

            // When
            ResponseEntity<ChatMessageResponse> response = chatController.sendMessage(mockRequest);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockResponse, response.getBody());
            verify(chatRoomRepository, times(1)).existsById(1L);
            verify(asyncChatRoomService, never()).createChatRoomAsync(any());
            verify(messagePersistence, times(1)).saveMessage(mockMessage);
            verify(messagePublisher, times(1)).publishMessage(mockMessage);
        }

        @DisplayName("채팅방이 존재하지 않는 경우 메시지 전송에 성공한다.")
        @Test
        void whenChatRoomDoesNotExist_thenSendMessageSuccess() throws ExecutionException, InterruptedException, TimeoutException {
            // Given
            when(chatRoomRepository.existsById(anyLong())).thenReturn(false);
            when(asyncChatRoomService.createChatRoomAsync(any(ChatMessageRequest.class)))
                    .thenReturn(CompletableFuture.completedFuture(new ChatRoomCreationResponse(
                            true,
                            "correlationId",
                            1L,
                            "asdfghjkl",
                            LocalDateTime.now(),
                            null
                    )));

            // When
            ResponseEntity<ChatMessageResponse> response = chatController.sendMessage(mockRequest);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockResponse, response.getBody());
            verify(chatRoomRepository, times(1)).existsById(1L);
            verify(asyncChatRoomService, times(1)).createChatRoomAsync(any(ChatMessageRequest.class));
            verify(messagePersistence, times(1)).saveMessage(mockMessage);
            verify(messagePublisher, times(1)).publishMessage(mockMessage);
        }
    }
}