package com.example.ticketingproject.domain.user.service;

import com.example.ticketingproject.auth.exception.AuthException;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findOneUser_success() {

        // given
        User user = User.builder()
                .name("이름")
                .email("test1@test.com")
                .password("12345678")
                .phone("01012341234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        // when
        GetUserResponse response = userService.findOneUser(user.getId());

        // then
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getEmail()).isEqualTo("test1@test.com");
        assertThat(response.getName()).isEqualTo("이름");
    }

    @Test
    void findOneUser_fail_userNotFound() {

        // given
        Long invalidId = 999L;

        // when & then
        assertThatThrownBy(() -> userService.findOneUser(invalidId))
                .isInstanceOf(UserException.class);
    }

    @Test
    void withdrawUser_success() {

        // given
        User user = User.builder()
                .name("이름")
                .email("test2@test.com")
                .password("12345678")
                .phone("01012341234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        // when
        userService.withdrawUser(user.getId());

        // then
        User withdrawUser = userRepository.findById(user.getId()).get();

        assertThat(withdrawUser.getUserStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(withdrawUser.getName()).isEqualTo("이**");
        assertThat(withdrawUser.getEmail()).contains("*");
        assertThat(withdrawUser.getPhone()).contains("****");
    }

    @Test
    void withdrawUser_fail_alreadyDeleted() {

        // given
        User user = User.builder()
                .name("이름")
                .email("test3@test.com")
                .password("1234")
                .phone("01012341234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.DELETED)
                .build();

        userRepository.save(user);

        // when & then
        assertThatThrownBy(() -> userService.withdrawUser(user.getId()))
                .isInstanceOf(UserException.class);
    }
}