package com.example.ticketingproject.domain.user.service;

import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.dto.UpdateUserRequest;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 내_정보_조회_성공_테스트() {

        // given
        User user = User.builder()
                .name("이름")
                .email("test1@test.com")
                .password("12345678")
                .phone("010-1234-1234")
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
    void 내_정보_조회_실패_테스트() {

        // given
        Long invalidId = 999L;

        // when & then
        assertThatThrownBy(() -> userService.findOneUser(invalidId))
                .isInstanceOf(UserException.class);
    }

    @Test
    void 회원_탈퇴_성공_테스트() {

        // given
        User user = User.builder()
                .name("이름")
                .email("test2@test.com")
                .password("12345678")
                .phone("010-1234-1234")
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
    void 회원_탈퇴_실패_테스트() {

        // given
        User user = User.builder()
                .name("이름")
                .email("test3@test.com")
                .password("12345678")
                .phone("010-1234-1234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.DELETED)
                .build();

        userRepository.save(user);

        // when & then
        assertThatThrownBy(() -> userService.withdrawUser(user.getId()))
                .isInstanceOf(UserException.class);
    }

    @Test
    void 내_정보_수정_성공_테스트() throws JsonProcessingException {

        // given
        User user = User.builder()
                .name("이름")
                .email("test4@test.com")
                .password("12345678")
                .phone("010-1234-1234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        String json = """
                {
                    "name": "이름수정",
                    "password": "새비밀번호1234",
                    "phone": "010-5678-5678"
                }
                """;

        UpdateUserRequest request = objectMapper.readValue(json, UpdateUserRequest.class);


        // when
        UpdateUserResponse response = userService.updateUser(user.getId(), request);

        // then
        assertEquals(user.getId(), response.getId());
        assertEquals("이름수정", response.getName());
        assertEquals("010-5678-5678", response.getPhone());

        User updateUser = userRepository.findById(user.getId()).orElseThrow();

        assertEquals("이름수정", updateUser.getName());
        assertTrue(passwordEncoder.matches("새비밀번호1234", updateUser.getPassword()));
        assertEquals("010-5678-5678", updateUser.getPhone());
    }
}