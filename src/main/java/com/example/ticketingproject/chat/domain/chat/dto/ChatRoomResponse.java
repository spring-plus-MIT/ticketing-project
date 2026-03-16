package com.example.ticketingproject.chat.domain.chat.dto;

import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomResponse {
    private Long roomId;
    private ChatRoomStatus status;
    private String creatorName;
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom room) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .status(room.getStatus())
                .creatorName(room.getCreator().getName())
                .createdAt(room.getCreatedAt())
                .build();
    }
}