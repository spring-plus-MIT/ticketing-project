package com.example.ticketingproject.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class DeletableEntity extends ModifiableEntity{

    private LocalDateTime deletedAt;

    public void deletedAt() {
        this.deletedAt = LocalDateTime.now();
    }
}
