package com.example.ticketingproject.auth.controller;

import com.example.ticketingproject.auth.dto.LoginRequest;
import com.example.ticketingproject.auth.dto.LoginResponse;
import com.example.ticketingproject.auth.dto.RegisterRequest;
import com.example.ticketingproject.auth.dto.RegisterResponse;
import com.example.ticketingproject.auth.service.AuthService;
import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                CommonResponse.success(SuccessStatus.REGISTER_SUCCESS, authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        String token = response.getAccessToken();
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(CommonResponse.success(SuccessStatus.LOGIN_SUCCESS, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout() {
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.LOGOUT_SUCCESS, null));
    }
}
