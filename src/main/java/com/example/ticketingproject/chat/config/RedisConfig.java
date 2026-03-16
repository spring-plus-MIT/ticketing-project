package com.example.ticketingproject.chat.config;

import com.example.ticketingproject.chat.pubsub.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 단일 채널 토픽 설정 (모든 채팅 메시지를 하나의 토픽으로 발행/구독)
     */
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chatroom");
    }

    /**
     * Redis에서 메시지가 발행(Publish)되면 처리할 리스너(Subscriber) 어댑터 설정
     * RedisSubscriber 객체의 'sendMessage' 메서드가 콜백으로 실행되도록 지정
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber redisSubscriber) {
        return new MessageListenerAdapter(redisSubscriber, "sendMessage");
    }

    /**
     * redis pub/sub 메시지를 처리하는 listener 설정
     * 생성한 컨테이너에 위에서 만든 리스너 어댑터와 토픽을 등록하여 연결
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListener(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic channelTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(listenerAdapter, channelTopic);

        return container;
    }

    /**
     * 어플리케이션에서 사용할 redisTemplate 설정
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return redisTemplate;
    }
}