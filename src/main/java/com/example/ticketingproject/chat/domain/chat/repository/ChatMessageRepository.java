package com.example.ticketingproject.chat.domain.chat.repository;

import com.example.ticketingproject.chat.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m JOIN FETCH m.sender " +
            "WHERE m.chatRoom.id = :roomId AND m.id < :lastMessageId " +
            "ORDER BY m.id DESC")
    List<ChatMessage> findMessagesBefore(
            @Param("roomId") Long roomId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );

    @Query("SELECT m FROM ChatMessage m JOIN FETCH m.sender " +
            "WHERE m.chatRoom.id = :roomId " +
            "ORDER BY m.id DESC")
    List<ChatMessage> findLatestMessages(
            @Param("roomId") Long roomId,
            Pageable pageable
    );
}