package com.example.ticketingproject.domain.seat.user.service;

import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class AdminUserServiceTest {
    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllUser_success() {

        // given
        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                    .name("user" + i)
                    .email("user" + i + "@test.com")
                    .password("12345678")
                    .phone("01012341234")
                    .balance(BigDecimal.valueOf(1000))
                    .userRole(UserRole.USER)
                    .userStatus(UserStatus.ACTIVE)
                    .build();

            userRepository.save(user);
        }

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<GetUserResponse> result = adminUserService.findAllUser(pageable);

        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getContent().get(0).getEmail()).contains("@test.com");
    }
}
