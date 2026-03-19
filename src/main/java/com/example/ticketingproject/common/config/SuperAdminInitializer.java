package com.example.ticketingproject.common.config;

import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.init.admin.enabled", havingValue = "true", matchIfMissing = true)
public class SuperAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${super-admin.email}")
    private String email;

    @Value("${super-admin.password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(email)) return; // 이미 존재하면 스킵

        User superAdmin = User.builder()
                .name("SUPER_ADMIN")
                .email(email)
                .password(passwordEncoder.encode(password))
                .phone("010-0000-0000")
                .balance(BigDecimal.ZERO)
                .userRole(UserRole.SUPER_ADMIN)
                .userStatus(UserStatus.ACTIVE) // 승인 불필요
                .build();

        userRepository.save(superAdmin);
        log.info("슈퍼 관리자 계정이 생성되었습니다: {}", email);
    }
}