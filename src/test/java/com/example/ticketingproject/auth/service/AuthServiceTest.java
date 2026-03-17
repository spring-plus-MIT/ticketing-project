package com.example.ticketingproject.auth.service;

import com.example.ticketingproject.auth.dto.RegisterRequest;
import com.example.ticketingproject.auth.dto.RegisterResponse;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "email", "admin@test.com");
        ReflectionTestUtils.setField(request, "name", "테스트어드민");
        ReflectionTestUtils.setField(request, "password", "password123");
        ReflectionTestUtils.setField(request, "phone", "010-1234-5678");
    }

    @Test
    void 어드민_회원가입_성공() {
        // given
        given(userRepository.existsByEmail("admin@test.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");

        User savedUser = User.builder()
                .name("테스트어드민")
                .email("admin@test.com")
                .password("encodedPassword")
                .phone("010-1234-5678")
                .balance(BigDecimal.ZERO)
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.PENDING)
                .build();
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        RegisterResponse response = authService.adminRegister(request);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User captured = captor.getValue();
        assertThat(captured.getUserRole()).isEqualTo(UserRole.ADMIN);
        assertThat(captured.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(captured.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captured.getPassword()).isEqualTo("encodedPassword");

        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN.getRoleName());
        assertThat(response.getStatus()).isEqualTo(UserStatus.PENDING.getStatusName());
    }

    @Test
    void 어드민_회원가입_실패_이메일_중복() {
        // given
        given(userRepository.existsByEmail("admin@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.adminRegister(request))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ErrorStatus.DUPLICATE_EMAIL.getMessage());

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
