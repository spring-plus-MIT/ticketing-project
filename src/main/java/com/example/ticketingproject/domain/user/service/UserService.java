package com.example.ticketingproject.domain.user.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public GetUserResponse findOneUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                )
        );

        return GetUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .balance(user.getBalance())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }

    @Transactional
    public UpdateUserResponse updateUser(Long userId) {
        return new UpdateUserResponse();
    }

    @Transactional
    public void withdrawUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                )
        );

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new UserException(
                    ErrorStatus.ALREADY_DELETED_USER.getHttpStatus(),
                    ErrorStatus.ALREADY_DELETED_USER
            );
        }
        user.withdraw();
    }
}
