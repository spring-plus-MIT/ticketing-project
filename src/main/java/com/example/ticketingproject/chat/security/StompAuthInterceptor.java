package com.example.ticketingproject.chat.security;

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

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
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

                } catch (Exception e) {
                    log.error("웹소켓 JWT 인증 실패: {}", e.getMessage());
                    throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                }
            } else {
                log.error("웹소켓 연결 요청에 Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
                throw new IllegalArgumentException("Authorization 헤더가 누락되었습니다.");
            }
        }
        return message;
    }
}