package com.example.ticketingproject.domain.user.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.payment.exception.PaymentException;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

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

    @NotBlank
    @Email
    @Length(max = 50)
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Pattern(regexp = "^010-[\\d*]{4}-\\d{4}$")
    private String phone;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @NotNull
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

    public BigDecimal pay(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new PaymentException(
                    ErrorStatus.INSUFFICIENT_BALANCE.getHttpStatus(),
                    ErrorStatus.INSUFFICIENT_BALANCE
            );
        }

        this.balance = this.balance.subtract(amount);

        return this.balance;
    }

    public void activate() {
        this.userStatus = UserStatus.ACTIVE;
    }
}
