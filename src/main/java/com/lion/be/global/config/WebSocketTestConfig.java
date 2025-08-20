package com.lion.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Profile("test")
public class WebSocketTestConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.messaging.stomp.broker-relay.host}")
    private String host;

    @Value("${spring.messaging.stomp.broker-relay.port}")
    private int port;

    @Value("${spring.messaging.stomp.broker-relay.system-login}")
    private String username;

    @Value("${spring.messaging.stomp.broker-relay.system-passcode}")
    private String password;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");

        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(host)
                .setRelayPort(port)
                .setSystemLogin(username)
                .setSystemPasscode(password)
                .setClientLogin(username)
                .setClientPasscode(password);
    }

}