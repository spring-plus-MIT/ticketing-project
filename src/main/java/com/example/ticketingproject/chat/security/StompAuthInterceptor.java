package com.example.ticketingproject.chat.security;

import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.chat.domain.chat.repository.ChatRoomRepository;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional(readOnly = true)
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {

            // 1. CONNECT: 소켓 연결 시 JWT 토큰 검증
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String header = accessor.getFirstNativeHeader("Authorization");

                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);

                    try {
                        Claims claims = jwtTokenProvider.getClaims(token);

                        Long userId = claims.get("userId", Long.class);
                        String email = claims.getSubject();
                        UserRole role = UserRole.valueOf(claims.get("role", String.class));

                        CustomUserDetails customUserDetails = new CustomUserDetails(userId, email, role);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        customUserDetails,
                                        null,
                                        customUserDetails.getAuthorities()
                                );

                        accessor.setUser(authentication);
                        log.info("[웹소켓 연결 성공] User ID: {}", userId);

                    } catch (Exception e) {
                        log.error("웹소켓 JWT 인증 실패: {}", e.getMessage());
                        throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                    }
                } else {
                    log.error("웹소켓 연결 요청에 Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
                    throw new IllegalArgumentException("Authorization 헤더가 누락되었습니다.");
                }
            }

            // 2. SUBSCRIBE: 채팅방 입장 시 권한 검증
            else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String destination = accessor.getDestination();

                if (destination != null && destination.startsWith("/sub/chat/room/")) {

                    Long roomId;
                    try {
                        roomId = Long.parseLong(destination.replace("/sub/chat/room/", ""));
                    } catch (NumberFormatException e) {
                        log.error("비정상적인 채팅방 구독 주소: {}", destination);
                        throw new IllegalArgumentException("잘못된 채팅방 주소입니다.");
                    }

                    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
                    if (auth == null) {
                        log.error("구독 요청 시 인증 정보가 없습니다.");
                        throw new IllegalArgumentException("인증 정보가 없습니다.");
                    }

                    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                    Long userId = userDetails.getId();
                    UserRole role = userDetails.getUserRole();

                    ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));

                    if (role != UserRole.ADMIN && !chatRoom.getCreator().getId().equals(userId)) {
                        log.warn("비정상적인 채팅방 구독 시도- User ID: {}, Room ID: {}", userId, roomId);
                        throw new IllegalArgumentException("해당 채팅방을 구독할 권한이 없습니다.");
                    }

                    log.info("[채팅방 구독 승인] User ID: {}, Room ID: {}", userId, roomId);
                }
            }
        }
        return message;
    }
}