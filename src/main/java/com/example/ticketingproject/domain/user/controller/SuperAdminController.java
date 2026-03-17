package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/super/admins")
public class SuperAdminController {

    private final UserService userService;

    @PostMapping("/{adminId}")
    public ResponseEntity<CommonResponse<Void>> activateAdmin(
            @PathVariable Long adminId
    ) {
        userService.activateUser(adminId);
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.UPDATE_SUCCESS, null));
    }

}
