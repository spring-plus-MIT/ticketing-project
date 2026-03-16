package com.example.ticketingproject.chat.domain.chat.service;

import com.example.ticketingproject.auth.exception.AuthException;
import com.example.ticketingproject.chat.domain.chat.dto.ChatRoomResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import com.example.ticketingproject.chat.domain.chat.exception.ChatException; // 추가!
import com.example.ticketingproject.chat.domain.chat.repository.ChatRoomRepository;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.ticketingproject.common.enums.ErrorStatus.CHAT_ROOM_NOT_FOUND;
import static com.example.ticketingproject.common.enums.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoomResponse createChatRoom(Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND.getHttpStatus(), USER_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.builder()
                .status(ChatRoomStatus.WAITING)
                .creator(creator)
                .build();

        chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.from(chatRoom);
    }

    public List<ChatRoomResponse> getChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND.getHttpStatus(), USER_NOT_FOUND));

        return chatRoomRepository.findAllByCreator(user).stream()
                .map(ChatRoomResponse::from)
                .toList();
    }

    @Transactional
    public void updateRoomStatus(Long roomId, ChatRoomStatus status) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND.getHttpStatus(), CHAT_ROOM_NOT_FOUND));

        room.changeStatus(status);
    }


}