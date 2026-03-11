package com.example.ticketingproject.domain.work.dto;

import com.example.ticketingproject.domain.work.entity.Work;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkResponse {
    private final Long id;
    private final String title;
    private final String category;
    private final String description;
    private final Long likeCount;

    public static WorkResponse from(Work work) {
        return WorkResponse.builder()
                .id(work.getId())
                .title(work.getTitle())
                .category(work.getCategory().getCategoryName())
                .description(work.getDescription())
                .likeCount(work.getLikeCount())
                .build();
    }
}
