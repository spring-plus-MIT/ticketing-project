package com.example.ticketingproject.chat.domain.chat.dto;

import com.example.ticketingproject.chat.domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static ChatMessageResponse createSystemMessage(Long roomId, String content) {
        return ChatMessageResponse.builder()
                .roomId(roomId)
                .senderId(0L)
                .senderName("시스템")
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}