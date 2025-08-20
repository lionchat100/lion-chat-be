package com.lion.be.global.interceptor;

import com.lion.be.auth.domain.StompPrincipal;
import com.lion.be.global.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT - Authorization Header: {}", jwtToken);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                try {
                    if (jwtTokenProvider.validateToken(token)) {
                        // [핵심 변경]
                        // 1. 기존 getAuthentication() 호출을 제거합니다.
                        // Authentication authentication = jwtTokenProvider.getAuthentication(token);

                        // 2. 1단계에서 추가한 메서드를 사용하여 토큰에서 User ID를 직접 추출합니다.
                        Long userId = jwtTokenProvider.getUserIdFromToken(token);

                        // 3. 추출한 User ID(문자열)를 이름으로 갖는 Principal 객체를 생성합니다.
                        StompPrincipal principal = new StompPrincipal(userId.toString());

                        // 4. 이 Principal을 현재 WebSocket 세션의 사용자로 설정합니다.
                        accessor.setUser(principal);

                        log.info("User ID '{}' connected. Session ID: {}", principal.getName(),
                                accessor.getSessionId());
                    }
                } catch (ExpiredJwtException e) {
                    log.info("Expired JWT Token: {}", e.getMessage());
                    throw new MessageDeliveryException("JWT_EXPIRED");
                } catch (Exception e) {
                    log.error("Invalid JWT Token: {}", e.getMessage());
                    throw new MessageDeliveryException("INVALID_TOKEN");
                }
            } else {
                throw new MessageDeliveryException("MISSING_TOKEN");
            }
        }
        return message;
    }

}