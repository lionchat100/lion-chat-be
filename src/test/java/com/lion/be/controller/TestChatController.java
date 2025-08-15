package com.lion.be.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.message.domain.dto.ChatMessageRequest;
import com.lion.be.chat.message.service.MessageProcessor;
import com.lion.be.global.aop.CheckRateLimitChat;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/api/test/chat")
@RequiredArgsConstructor
public class TestChatController {

    private final MessageProcessor messageProcessor;

    @PostMapping("/messages")
    @CheckRateLimitChat
    public ResponseEntity<Void> sendMessageForTest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ChatMessageRequest request) {
        messageProcessor.processIncomingMessage(request, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

}