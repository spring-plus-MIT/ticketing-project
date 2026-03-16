package com.example.ticketingproject.chat.domain.chat.controller;

import com.example.ticketingproject.chat.domain.chat.dto.ChatRoomResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import com.example.ticketingproject.chat.domain.chat.service.AdminChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/chat/rooms")
public class AdminChatRoomController {

    private final AdminChatRoomService adminChatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getAdminRooms(
            @RequestParam(required = false) ChatRoomStatus status
    ) {
        List<ChatRoomResponse> responses = adminChatRoomService.getAdminChatRooms(status);
        return ResponseEntity.ok(responses);
    }
}