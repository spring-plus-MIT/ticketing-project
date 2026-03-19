package com.example.ticketingproject.domain.user.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.payment.exception.PaymentException;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Column(nullable = false)
    @Length(min = 1, max = 30, message = "이름은 1자 이상 30자 이하로 입력해주세요.")
    private String name;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Column(nullable = false, unique = true)
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Length(max = 50, message = "이메일은 최대 50자까지 가능합니다.")
    private String email;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Column(nullable = false)
    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Column(nullable = false)
    @Length(max = 14, message = "전화번호 형식을 확인해주세요.")
    private String phone;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @DecimalMin(value = "0.0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
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
