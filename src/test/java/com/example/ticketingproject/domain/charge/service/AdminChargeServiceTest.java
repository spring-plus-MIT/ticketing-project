package com.example.ticketingproject.domain.charge.service;

import com.example.ticketingproject.domain.charge.dto.ChargeRequest;
import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.entity.Charge;
import com.example.ticketingproject.domain.charge.repository.ChargeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminChargeServiceTest {

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminChargeService adminChargeService;

    private User admin;
    private User user;
    private Charge charge;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .name("admin")
                .email("test@123.com")
                .password("1234")
                .phone("010-1234-1234")
                .balance(BigDecimal.valueOf(100))
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(admin, "id", 1L);

        user = User.builder()
                .name("user")
                .email("test@456.com")
                .password("5678")
                .phone("010-1234-5678")
                .balance(BigDecimal.valueOf(100))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(user, "id", 2L);

        charge = Charge.builder()
                .admin(admin)
                .user(user)
                .amount(BigDecimal.valueOf(500))
                .balanceAfterCharge(user.getBalance().add(BigDecimal.valueOf(500)))
                .build();
        ReflectionTestUtils.setField(charge, "id", 1L);
    }

    @Test
    void charge_성공() {
        ChargeRequest request = mock(ChargeRequest.class);
        given(request.getAmount()).willReturn(BigDecimal.valueOf(500));

        given(userRepository.findById(1L)).willReturn(Optional.of(admin));
        given(userRepository.findById(2L)).willReturn(Optional.of(user));
        given(chargeRepository.save(any(Charge.class))).willReturn(charge);

        // when
        ChargeResponse response = adminChargeService.charge(1L, 2L, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(response.getBalanceAfterCharge()).isEqualByComparingTo(BigDecimal.valueOf(600));

        then(userRepository).should(times(1)).findById(1L);
        then(userRepository).should(times(1)).findById(2L);
        then(chargeRepository).should(times(1)).save(any(Charge.class));
    }

    @Test
    void charge_실패_어드민없음() {
        // given
        ChargeRequest request = mock(ChargeRequest.class);

        given(userRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminChargeService.charge(99L, 2L, request))
                .isInstanceOf(UserException.class);

        then(userRepository).should(never()).findById(2L);
        then(chargeRepository).should(never()).save(any());
    }

    @Test
    void charge_실패_유저없음() {
        ChargeRequest request = mock(ChargeRequest.class);

        given(userRepository.findById(1L)).willReturn(Optional.of(admin));
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminChargeService.charge(1L, 99L, request))
                .isInstanceOf(UserException.class);

        then(chargeRepository).should(never()).save(any());
    }

    @Test
    void findAll_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Charge> chargePage = new PageImpl<>(List.of(charge), pageable, 1);

        given(chargeRepository.findAll(pageable)).willReturn(chargePage);

        // when
        Page<ChargeResponse> result = adminChargeService.findAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));

        then(chargeRepository).should(times(1)).findAll(pageable);
    }

    @Test
    void findAll_빈목록() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        given(chargeRepository.findAll(pageable)).willReturn(Page.empty(pageable));

        // when
        Page<ChargeResponse> result = adminChargeService.findAll(pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void findAllByUserId_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Charge> chargePage = new PageImpl<>(List.of(charge), pageable, 1);

        given(chargeRepository.findAllByUserId(2L, pageable)).willReturn(chargePage);

        // when
        Page<ChargeResponse> result = adminChargeService.findAllByUserId(2L, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));

        then(chargeRepository).should(times(1)).findAllByUserId(2L, pageable);
    }

    @Test
    void findAllByUserId_빈목록() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        given(chargeRepository.findAllByUserId(2L, pageable)).willReturn(Page.empty(pageable));

        // when
        Page<ChargeResponse> result = adminChargeService.findAllByUserId(2L, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}