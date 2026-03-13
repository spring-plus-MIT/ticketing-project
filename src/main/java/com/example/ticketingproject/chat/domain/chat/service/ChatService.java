package com.example.ticketingproject.chat.domain.chat.service;

import com.example.ticketingproject.chat.domain.chat.entity.ChatRoomStatus;
import com.example.ticketingproject.chat.domain.chat.exception.ChatException; // 추가!
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageRequest;
import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatMessage;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.repository.ChatMessageRepository;
import com.example.ticketingproject.chat.domain.chat.repository.ChatRoomRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorStatus.USER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ChatException(HttpStatus.NOT_FOUND, ErrorStatus.CHAT_ROOM_NOT_FOUND));

        if (sender.getUserRole() != UserRole.ADMIN && !chatRoom.getCreator().getId().equals(senderId)) {
            throw new ChatException(HttpStatus.FORBIDDEN, ErrorStatus.FORBIDDEN_CHAT_ROOM);
        }

        if (chatRoom.getStatus() == ChatRoomStatus.COMPLETED) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorStatus.CHAT_ROOM_ALREADY_COMPLETED);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .build();

        chatMessageRepository.save(message);

        if (chatRoom.getStatus() == ChatRoomStatus.WAITING && sender.getUserRole() == UserRole.ADMIN) {
            chatRoom.changeStatus(ChatRoomStatus.IN_PROGRESS);
        }

        return ChatMessageResponse.from(message);
    }

    public List<ChatMessageResponse> getMessageHistory(Long roomId, Long lastMessageId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<ChatMessage> messages;

        if (lastMessageId == null) {
            messages = chatMessageRepository.findLatestMessages(roomId, pageable);
        } else {
            messages = chatMessageRepository.findMessagesBefore(roomId, lastMessageId, pageable);
        }

        return messages.stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
}