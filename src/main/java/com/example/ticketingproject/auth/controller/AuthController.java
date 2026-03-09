package com.example.ticketingproject.auth.controller;

import com.example.ticketingproject.auth.dto.LoginRequest;
import com.example.ticketingproject.auth.dto.LoginResponse;
import com.example.ticketingproject.auth.dto.RegisterRequest;
import com.example.ticketingproject.auth.dto.RegisterResponse;
import com.example.ticketingproject.auth.service.AuthService;
import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
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
    public ResponseEntity<CommonResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(
                        SuccessStatus.REGISTER_SUCCESS.getStatusCode(),
                        SuccessStatus.REGISTER_SUCCESS.getMessage(),
                        authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        String token = response.getAccessToken();
        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", "Bearer " + token)
                .body(CommonResponse.success(
                        SuccessStatus.LOGIN_SUCCESS.getStatusCode(),
                        SuccessStatus.LOGIN_SUCCESS.getMessage(),
                        response));
    }
}
