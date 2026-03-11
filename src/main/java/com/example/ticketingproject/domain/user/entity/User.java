package com.example.ticketingproject.domain.user.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends DeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(max = 30)
    private String name;

    @Column(unique = true)
    @Length(max = 50)
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8)
    private String password;

    @Length(max = 14)
    @NotBlank
    private String phone;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Builder
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
        delete();
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

        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }

    public void update(String name, String password, String phone) {
        this.name = name;
        this.password = password;
        this.phone = phone;
    }

    public void changeBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
