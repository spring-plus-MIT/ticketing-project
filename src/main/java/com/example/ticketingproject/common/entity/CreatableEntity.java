package com.example.ticketingproject.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners((AuditingEntityListener.class))
public class CreatableEntity {

    @CreatedDate
    private LocalDateTime createdAt;
}
