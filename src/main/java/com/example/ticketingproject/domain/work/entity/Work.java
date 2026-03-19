package com.example.ticketingproject.domain.work.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.domain.work.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "works")
public class Work extends ModifiableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 1, max = 100)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @Length(min = 1, max = 255)
    private String description;

    @Min(value = 0)
    private Long likeCount;

    @Builder
    public Work (String title, Category category, String description, Long likeCount) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.likeCount = likeCount;
    }

    public void update(String title,  Category category, String description) {
        this.title = title;
        this.category = category;
        this.description = description;
    }
}
