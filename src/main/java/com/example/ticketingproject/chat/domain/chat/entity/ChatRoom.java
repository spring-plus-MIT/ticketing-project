package com.example.ticketingproject.chat.domain.chat.entity;

import com.example.ticketingproject.chat.domain.chat.exception.ChatException;
import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_rooms")
public class ChatRoom extends DeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Builder
    public ChatRoom(ChatRoomStatus status, User creator) {
        this.status = status;
        this.creator = creator;
    }

    public void changeStatus(ChatRoomStatus newStatus) {

        if (this.status == ChatRoomStatus.COMPLETED && newStatus != ChatRoomStatus.COMPLETED) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorStatus.INVALID_STATUS_TRANSITION);
        }

        this.status = newStatus;
    }
}