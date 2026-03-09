package com.example.ticketingproject.domain.user.entity;

import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private BigDecimal balance;
    private UserRole userRole;
    private UserStatus userStatus;

    public User (String name, String email, String password, String phone, BigDecimal balance, UserRole userRole, UserStatus userStatus) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.balance = balance;
        this.userRole = userRole;
        this.userStatus = userStatus;
    }


}
