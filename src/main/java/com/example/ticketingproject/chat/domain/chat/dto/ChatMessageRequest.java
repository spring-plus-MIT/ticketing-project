package com.example.ticketingproject.chat.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {

    private Long roomId;

    @NotBlank(message = "메시지 내용은 비어 있을 수 없습니다.")
    private String content;

}