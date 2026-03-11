package com.example.ticketingproject.domain.like.dto;

import com.example.ticketingproject.domain.like.entity.Like;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponse {
    private final Long likeId;
    private final Long userId;
    private final Long workId;

    public static LikeResponse from(Like like) {
        return LikeResponse.builder()
                .likeId(like.getId())
                .userId(like.getUser().getId())
                .workId(like.getWork().getId())
                .build();
    }
}
