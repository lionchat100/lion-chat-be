package com.lion.be.global.interceptor;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.global.util.JwtTokenProvider;
import java.util.Objects;
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

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 연결 요청(CONNECT) 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT - Authorization Header: {}", jwtToken);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                // JWT 토큰 유효성 검증
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    log.info("auth: {}", auth);
                    accessor.setUser(auth); // WebSocket 세션에 사용자 정보 저장
                    log.info("accessor: {}", accessor);
                    log.info("User '{}' connected.", auth.getName());
                } else {
                    log.warn("Invalid JWT token received.");
                    // 연결을 거부하려면 여기서 예외를 던질 수 있습니다.
                }
            }
        }
        // 메시지 발행(SEND) 요청 처리
        else if (StompCommand.SEND.equals(accessor.getCommand())) {
            // 연결 시 저장된 사용자 정보(Principal)를 가져옴
            Authentication authentication = (Authentication) accessor.getUser();
            if (authentication != null) {
                accessor.setHeader("simpUser", authentication); // 'simpUser' 헤더에 인증 정보 설정
                log.debug("Propagating user '{}' for SEND command.", authentication.getName());
            } else {
                log.warn("SEND command from an unauthenticated session.");
            }
        }
        return message;
    }
}