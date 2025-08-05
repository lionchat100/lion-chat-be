package com.lion.be.global.config;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

public class StompEventTracker {
    // 사용자 세션 정보를 메모리에 간단히 저장 (실제 서비스에서는 Redis 등 외부 저장소 사용 권장)
    // private final Map<String, UserPrincipal> connectedUsers = new ConcurrentHashMap<>();

    @Component
    public static class StompConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {
        @Override
        public void onApplicationEvent(SessionConnectedEvent event) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            // CONNECT 프레임에서 인증 정보를 가져옴 (ChannelInterceptor에서 주입)
            String username = accessor.getUser().getName();
            String sessionId = accessor.getSessionId();
            // connectedUsers.put(sessionId, (UserPrincipal) accessor.getUser().getPrincipal());

            System.out.println("Client Connected: " + accessor);
        }
    }

    @Component
    public static class StompSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {
        @Override
        public void onApplicationEvent(SessionSubscribeEvent event) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String username = accessor.getUser().getName();
            String destination = accessor.getDestination();

            System.out.println("Client Subscribe: " + accessor);
        }
    }

    @Component
    public static class StompUnsubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent> {
        @Override
        public void onApplicationEvent(SessionUnsubscribeEvent event) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String username = accessor.getUser().getName();
            String destination = accessor.getDestination();

            System.out.println("Client Unubscribe: " + accessor);
        }
    }



    @Component
    public static class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {
        @Override
        public void onApplicationEvent(SessionDisconnectEvent event) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = accessor.getSessionId();
            // 여기서 sessionId를 이용해 사용자를 식별하고 리소스를 정리하는 로직을 수행
            System.out.println("Client Disconnected: " + accessor);
        }
    }
}
