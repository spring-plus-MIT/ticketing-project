package com.example.ticketingproject.chat.pubsub;

import com.example.ticketingproject.chat.domain.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, ChatMessageResponse message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        log.info("Redis에 메시지 발행 완료 - RoomId: {}, Message: {}", message.getRoomId(), message.getContent());
    }
}