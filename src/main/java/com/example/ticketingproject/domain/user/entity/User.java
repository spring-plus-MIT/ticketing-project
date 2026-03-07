package com.example.ticketingproject.domain.user.entity;

import com.example.ticketingproject.common.entity.BaseEntity;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
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
    private LocalDateTime deletedAt;


    public User (String name, String email, String password, String phone, BigDecimal balance, UserRole userRole, UserStatus userStatus) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.balance = balance;
        this.userRole = userRole;
        this.userStatus = userStatus;
    }

    public void withdraw() {
        this.userStatus = UserStatus.DELETED;
        this.name = maskName(this.name);
        this.email = maskEmail(this.email);
        this.phone = maskPhone(this.phone);
        this.deletedAt = LocalDateTime.now();

    }

    public String maskName(String name) {
        if (name == null) {
            return null;
        }

        return name.charAt(0) + "**";
    }

    public String maskEmail(String email) {
        if (email == null) {
            return null;
        }

        int index = email.indexOf("@");
        String twoWords = email.substring(0, 2);
        int maskLength = index - 2;
        String mask = "*".repeat(maskLength);

        return twoWords + mask + email.substring(index);
    }

    public String maskPhone(String phone) {
        if (phone == null) {
            return null;
        }

        return phone.substring(0, 2) + "-****-" + phone.substring(phone.length() - 4);
    }
}
