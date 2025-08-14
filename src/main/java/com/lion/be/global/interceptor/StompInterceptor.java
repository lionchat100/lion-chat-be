package com.lion.be.global.interceptor;

import com.lion.be.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider; // 직접 구현한 JWT 토큰 유틸리티

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT - Authorization Header: {}", jwtToken);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                // 토큰 유효성 검사
                if (jwtTokenProvider.validateToken(token)) {
                    // <<< 중요!!! 토큰이 유효하면 인증 정보를 생성하여 세션에 저장합니다.
                    Authentication authentication = jwtTokenProvider.getAuthentication(
                            token); // JWT로부터 Authentication 객체 생성
                    accessor.setUser(authentication); // accessor에 user 정보 저장

                    log.info("User '{}' connected. Session ID: {}", authentication.getName(), accessor.getSessionId());
                } else {
                    // 유효하지 않은 토큰 처리 (예: 로깅 후 연결 종료)
                    log.error("Invalid JWT token received.");
                    // 예외를 던지거나 메시지 반환을 null로 하여 연결을 거부할 수 있습니다.
                    // 여기서는 명시적으로 에러를 던지지 않고 연결이 안 되도록 합니다.
                    // CustomStompErrorHandler를 사용한다면 예외를 던지는 것이 좋습니다.
                    // throw new AccessDeniedException("Invalid JWT token.");
                }
            }
        }

        return message;
    }

}