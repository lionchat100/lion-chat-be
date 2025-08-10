package com.lion.be.acceptance.chat;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.controller.ChatRoomController;
import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.chat.service.ChatRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // 이 부분을 추가해야 합니다.
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // 이 부분을 추가해야 합니다.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // ✨이 어노테이션을 추가해야 합니다.✨
@DisplayName("채팅방 컨트롤러 기능 테스트")
class ChatRoomAcceptTest {

    @InjectMocks
    private ChatRoomController chatRoomController;

    @Mock
    private ChatRoomService chatRoomService;

    private UserPrincipal mockUserPrincipal;
    private List<ChatRoom> mockChatRooms;

    @BeforeEach
    void setUp() {
        mockUserPrincipal = new UserPrincipal(
                1L,
                "test@test.com",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("name", "TestUser")
        );

        mockChatRooms = List.of(
                new ChatRoom(1L, false, LocalDateTime.now(), new ArrayList<>(), "최근 메시지1", Instant.now()),
                new ChatRoom(2L, false, LocalDateTime.now().minusDays(1), new ArrayList<>(), "최근 메시지2", Instant.now())
        );
    }

    @Test
    @DisplayName("사용자의 채팅방 목록 조회 시, 상태코드 200과 목록을 반환한다.")
    void when_getChatRoomList_then_return_200_and_list() {
        // Given
        when(chatRoomService.getChatRooms(anyLong())).thenReturn(mockChatRooms);

        // When
        ResponseEntity<List<ChatRoom>> response = chatRoomController.getMyChatRoomList(mockUserPrincipal);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockChatRooms.size(), response.getBody().size());
        assertEquals(mockChatRooms.get(0).getId(), response.getBody().get(0).getId());
        assertEquals(mockChatRooms.get(1).getId(), response.getBody().get(1).getId());
    }
}