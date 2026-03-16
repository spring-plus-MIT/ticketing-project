package com.example.ticketingproject.chat.domain.chat.controller;

import com.example.ticketingproject.chat.domain.chat.dto.ChatRoomResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import com.example.ticketingproject.chat.domain.chat.service.ChatRoomService;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 1. 문의 채팅방 생성 API (파라미터 없음)
     */
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ChatRoomResponse response = chatRoomService.createChatRoom(userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 내 채팅방 목록 조회 API (어드민은 전체 조회)
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ChatRoomResponse> responses = chatRoomService.getChatRooms(userDetails.getId());
        return ResponseEntity.ok(responses);
    }

    /**
     * 3. CS 문의 상태 변경 API
     */
    @PatchMapping("/{roomId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long roomId,
            @RequestParam ChatRoomStatus status
    ) {
        chatRoomService.updateRoomStatus(roomId, status);
        return ResponseEntity.ok().build();
    }
}