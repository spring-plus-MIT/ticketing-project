package com.example.ticketingproject.domain.user.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.dto.UpdateUserRequest;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User validateUserId(Long userId) throws UserException {
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
        return user;
    }

    public GetUserResponse findOneUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                )
        );

        return GetUserResponse.from(user);
    }

    @Transactional
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                )
        );

        user.update(
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone()
        );

        return UpdateUserResponse.from(user);
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

    @Transactional
    public void activateUser(Long adminId) {
        User user = validateUserId(adminId);
        user.activate();
    }
}
