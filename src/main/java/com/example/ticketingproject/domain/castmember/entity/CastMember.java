package com.example.ticketingproject.domain.castmember.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "castmembers")
public class CastMember extends ModifiableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performance_session_id", nullable = false)
    private PerformanceSession performanceSession;

    @NotBlank
    @Length(max = 50)
    private String name;

    @NotBlank
    @Length(max = 50)
    private String roleName;

    @Builder
    public CastMember(PerformanceSession performanceSession, String name, String roleName) {
        this.performanceSession = performanceSession;
        this.name = name;
        this.roleName = roleName;
    }

    public void update(String name, String roleName) {
        this.name = name;
        this.roleName = roleName;
    }
}

