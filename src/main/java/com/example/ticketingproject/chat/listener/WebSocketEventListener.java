package com.example.ticketingproject.chat.listener;

import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    /**
     * 1. 유저 입장 감지 (채팅방 구독 시점)
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/sub/chat/room/")) {
            String roomIdStr = destination.replace("/sub/chat/room/", "");
            Long roomId = Long.parseLong(roomIdStr);

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) event.getUser();
            if (auth != null) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                Long userId = userDetails.getId();

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
                String realName = user.getName();

                if (accessor.getSessionAttributes() != null) {
                    accessor.getSessionAttributes().put("roomId", roomId);
                    accessor.getSessionAttributes().put("username", realName);
                }

                ChatMessageResponse systemMessage = ChatMessageResponse.createSystemMessage(roomId, realName + "님이 입장하셨습니다.");

                messagingTemplate.convertAndSend(destination, systemMessage);
                log.info("[채팅방 입장] 방 번호: {}, 유저명: {}", roomId, realName);
            }
        }
    }

    /**
     * 2. 유저 퇴장 감지 (웹소켓 연결 종료 시점)
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getSessionAttributes() != null) {
            Long roomId = (Long) accessor.getSessionAttributes().get("roomId");
            String username = (String) accessor.getSessionAttributes().get("username"); // 위에서 저장한 진짜 이름

            if (roomId != null && username != null) {

                ChatMessageResponse systemMessage = ChatMessageResponse.createSystemMessage(roomId, username + "님이 퇴장하셨습니다.");

                messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, systemMessage);
                log.info("[채팅방 퇴장] 방 번호: {}, 유저명: {}", roomId, username);
            }
        }
    }
}