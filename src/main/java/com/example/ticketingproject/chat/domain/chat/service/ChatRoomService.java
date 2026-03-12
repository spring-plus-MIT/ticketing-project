package com.example.ticketingproject.chat.domain.chat.service;

import com.example.ticketingproject.chat.domain.chat.dto.ChatRoomResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import com.example.ticketingproject.chat.domain.chat.repository.ChatRoomRepository;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 문의 채팅방 생성 (무조건 WAITING 상태)
     */
    @Transactional
    public ChatRoomResponse createChatRoom(Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, ErrorStatus.USER_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.builder()
                .status(ChatRoomStatus.WAITING)
                .creator(creator)
                .build();

        chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.from(chatRoom);
    }

    /**
     * 권한별 채팅방 목록 조회 (어드민 전체, 유저 본인)
     */
    public List<ChatRoomResponse> getChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, ErrorStatus.USER_NOT_FOUND));

        List<ChatRoom> rooms;
        if (user.getUserRole() == UserRole.ADMIN) {
            rooms = chatRoomRepository.findAll(); // 모든 어드민은 모든 방을 볼 수 있음
        } else {
            rooms = chatRoomRepository.findAllByCreator(user);
        }

        return rooms.stream()
                .map(ChatRoomResponse::from)
                .toList();
    }

    /**
     * 문의 상태 변경
     */
    @Transactional
    public void updateRoomStatus(Long roomId, ChatRoomStatus status) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, ErrorStatus.USER_NOT_FOUND));

        room.changeStatus(status);
    }
}