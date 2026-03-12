package com.example.ticketingproject.chat.domain.chat.controller;

import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageRequest;
import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageResponse;
import com.example.ticketingproject.chat.domain.chat.service.ChatService;
import com.example.ticketingproject.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // 1. STOMP: 실시간 메시지 전송
    @MessageMapping("/chat/send")
    public void sendMessage(
            @Valid @Payload ChatMessageRequest request, Principal principal) {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;

        CustomUserDetails userDetails = (CustomUserDetails) authToken.getPrincipal();
        Long senderId = userDetails.getId();

        ChatMessageResponse response = chatService.saveMessage(request, senderId);

        messagingTemplate.convertAndSend("/sub/chat/room/" + request.getRoomId(), response);
    }

    @GetMapping("/api/chat/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long lastMessageId,
            @RequestParam(defaultValue = "30") int size
    ) {
        List<ChatMessageResponse> history = chatService.getMessageHistory(roomId, lastMessageId, size);
        return ResponseEntity.ok(history);
    }
}