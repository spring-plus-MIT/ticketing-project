package com.example.ticketingproject.common.config;

import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SuperAdminInitializerTest {

    @InjectMocks
    private SuperAdminInitializer superAdminInitializer;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(superAdminInitializer, "email", "superadmin@test.com");
        ReflectionTestUtils.setField(superAdminInitializer, "password", "superpassword123");
    }

    @Test
    void 슈퍼_관리자_계정이_없으면_생성한다() throws Exception {
        // given
        given(userRepository.existsByEmail("superadmin@test.com")).willReturn(false);
        given(passwordEncoder.encode("superpassword123")).willReturn("encodedPassword");

        // when
        superAdminInitializer.run(null);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("SUPER_ADMIN");
        assertThat(saved.getEmail()).isEqualTo("superadmin@test.com");
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        assertThat(saved.getUserRole()).isEqualTo(UserRole.SUPER_ADMIN);
        assertThat(saved.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(saved.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void 슈퍼_관리자_계정이_이미_존재하면_생성을_스킵한다() throws Exception {
        // given
        given(userRepository.existsByEmail("superadmin@test.com")).willReturn(true);

        // when
        superAdminInitializer.run(null);

        // then
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
