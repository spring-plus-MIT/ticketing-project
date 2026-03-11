package com.example.ticketingproject.domain.work.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.domain.work.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "works")
public class Work extends ModifiableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(max = 100)
    private String title;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

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
