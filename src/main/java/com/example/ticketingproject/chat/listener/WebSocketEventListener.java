package com.example.ticketingproject.chat.listener;

import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageResponse;
import com.example.ticketingproject.chat.domain.chat.entity.ChatMessage;
import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.repository.ChatMessageRepository;
import com.example.ticketingproject.chat.domain.chat.repository.ChatRoomRepository;
import com.example.ticketingproject.chat.pubsub.RedisPublisher;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisPublisher redisPublisher;
    private final ChannelTopic channelTopic;

    /**
     * 1. 유저 입장 감지 (채팅방 구독 시점)
     */
    @EventListener
    @Transactional
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/sub/chat/room/")) {
            Long roomId;
            try {
                roomId = Long.parseLong(destination.replace("/sub/chat/room/", ""));
            } catch (NumberFormatException e) {
                log.error("잘못된 채팅방 구독 주소 형식: {}", destination);
                return;
            }

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) event.getUser();
            if (auth != null) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                Long userId = userDetails.getId();

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
                ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

                String content = user.getName() + "님이 입장하셨습니다.";

                if (accessor.getSessionAttributes() != null) {
                    accessor.getSessionAttributes().put("roomId", roomId);
                    accessor.getSessionAttributes().put("userId", userId);
                    accessor.getSessionAttributes().put("username", user.getName());
                }

                ChatMessage message = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .sender(user)
                        .content(content)
                        .build();
                chatMessageRepository.save(message);


                ChatMessageResponse systemMessage = ChatMessageResponse.createSystemMessage(roomId, content);
                redisPublisher.publish(channelTopic, systemMessage);

                log.info("[채팅방 입장 DB 저장 및 발행 완료] 방 번호: {}, 유저명: {}", roomId, user.getName());
            }
        }
    }

    /**
     * 2. 유저 퇴장 감지 (웹소켓 연결 종료 시점)
     */
    @EventListener
    @Transactional // DB 저장이 일어나므로 추가
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getSessionAttributes() != null) {
            Long roomId = (Long) accessor.getSessionAttributes().get("roomId");
            Long userId = (Long) accessor.getSessionAttributes().get("userId");
            String username = (String) accessor.getSessionAttributes().get("username");

            if (roomId != null && userId != null && username != null) {

                User user = userRepository.findById(userId).orElse(null);
                ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);

                if (user != null && chatRoom != null) {
                    String content = username + "님이 퇴장하셨습니다.";

                    ChatMessage message = ChatMessage.builder()
                            .chatRoom(chatRoom)
                            .sender(user)
                            .content(content)
                            .build();
                    chatMessageRepository.save(message);

                    ChatMessageResponse systemMessage = ChatMessageResponse.createSystemMessage(roomId, content);
                    redisPublisher.publish(channelTopic, systemMessage);

                    log.info("[채팅방 퇴장 DB 저장 및 발행 완료] 방 번호: {}, 유저명: {}", roomId, username);
                }
            }
        }
    }
}