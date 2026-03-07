package com.example.ticketingproject.auth.service;

import com.example.ticketingproject.auth.dto.LoginRequest;
import com.example.ticketingproject.auth.dto.LoginResponse;
import com.example.ticketingproject.auth.dto.RegisterRequest;
import com.example.ticketingproject.auth.dto.RegisterResponse;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        boolean existence = userRepository.existsByEmail(request.getEmail());
        if (existence) {
            throw new IllegalArgumentException(ErrorStatus.DUPLICATE_EMAIL.getMessage());
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                BigDecimal.ZERO,
                UserRole.USER,
                UserStatus.ACTIVE
        );
        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getUserRole().getRoleName(),
                savedUser.getUserStatus().getStatusName()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 유저입니다."));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails customUserDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtTokenProvider.createToken(customUserDetails);

        return new LoginResponse(token);
    }
}
