package com.lion.be.global.interceptor;

import com.lion.be.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 1. CONNECT 요청 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    accessor.setUser(auth);
                    log.info("User '{}' connected. Session ID: {}", auth.getName(), accessor.getSessionId());
                }
            }
        }
        // 2. SEND 요청 처리
        else if (StompCommand.SEND.equals(accessor.getCommand())) {
            Authentication auth = (Authentication) accessor.getUser();
            if (auth != null) {
                accessor.setHeader("simpUser", auth);
            }
        }

        // accessor.getMessage()가 아닌, MessageBuilder를 사용해 메시지를 재생성합니다.
        // 원본 메시지의 페이로드(내용)와 accessor에서 수정한 헤더를 합쳐
        // 새로운 Message 객체를 만들어 반환합니다.
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

}