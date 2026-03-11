package com.example.ticketingproject.domain.charge.service;

import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.entity.Charge;
import com.example.ticketingproject.domain.charge.repository.ChargeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeServiceTest {

    @Mock
    private ChargeRepository chargeRepository;

    @InjectMocks
    private ChargeService chargeService;

    private User admin;
    private User user;
    private Charge charge;

    @BeforeEach
    void setUp() {

        admin = User.builder()
                .name("admin")
                .email("admin@test.com")
                .password("1234")
                .phone("010-1234-1234")
                .balance(BigDecimal.valueOf(100))
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(admin, "id", 1L);

        user = User.builder()
                .name("user")
                .email("user@test.com")
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
    void findAll_성공() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Charge> chargePage = new PageImpl<>(List.of(charge), pageable, 1);

        given(chargeRepository.findAllByUserId(2L, pageable)).willReturn(chargePage);

        Page<ChargeResponse> result = chargeService.findAll(2L, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(result.getContent().get(0).getBalanceAfterCharge()).isEqualByComparingTo(BigDecimal.valueOf(600));

        then(chargeRepository).should(times(1)).findAllByUserId(2L, pageable);
    }

    @Test
    void findAll_빈목록() {
        Pageable pageable = PageRequest.of(0, 10);

        given(chargeRepository.findAllByUserId(2L, pageable)).willReturn(Page.empty(pageable));

        Page<ChargeResponse> result = chargeService.findAll(2L, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}