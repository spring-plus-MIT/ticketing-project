package com.example.ticketingproject.chat.domain.chat.service;

import com.example.ticketingproject.chat.domain.chat.dto.ChatRoomResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import com.example.ticketingproject.chat.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoomResponse> getAdminChatRooms(ChatRoomStatus status) {
        List<ChatRoom> rooms;

        if (status != null) {
            rooms = chatRoomRepository.findAllByStatus(status);
        } else {
            rooms = chatRoomRepository.findAll();
        }

        return rooms.stream()
                .map(ChatRoomResponse::from)
                .toList();
    }
}
