package com.example.ticketingproject.domain.user.service;

import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminUserServiceTest {
    @InjectMocks
    private AdminUserService adminUserService;

    @Mock
    private UserRepository userRepository;

    @Test
    void 유저_전체_조회_성공_테스트() {

        // given
        List<User> users = new ArrayList<>();
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

            ReflectionTestUtils.setField(user, "id", (long) i);
            users.add(user);
        }

        PageRequest pageable = PageRequest.of(0, 10);

        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // when
        Page<GetUserResponse> result = adminUserService.findAllUser(pageable);

        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getContent().get(0).getEmail()).contains("@test.com");
    }
}
