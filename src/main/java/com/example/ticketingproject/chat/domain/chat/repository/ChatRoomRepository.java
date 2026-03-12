package com.example.ticketingproject.chat.domain.chat.repository;

import com.example.ticketingproject.chat.domain.chat.entity.ChatRoom;
import com.example.ticketingproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.creator")
    List<ChatRoom> findAll();

    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.creator WHERE c.creator = :creator")
    List<ChatRoom> findAllByCreator(@Param("creator") User creator);

}