package com.example.ticketingproject.domain.work.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.domain.work.enums.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Length(max = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    private BigDecimal minPrice;

    private Long likeCount;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

}
