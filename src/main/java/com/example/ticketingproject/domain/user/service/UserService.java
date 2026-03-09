package com.example.ticketingproject.domain.user.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<GetUserResponse> findAllUser(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> new GetUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBalance(),
                user.getUserRole(),
                user.getUserStatus(),
                user.getCreatedAt(),
                user.getModifiedAt()
        ));
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

    @Transactional(readOnly = true)
    public Page<GetUserResponse> findAllDeletedUser(Pageable pageable) {
        Page<User> users = userRepository.findAllByUserStatus(UserStatus.DELETED, pageable);

        return users.map(user -> new GetUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBalance(),
                user.getUserRole(),
                user.getUserStatus(),
                user.getCreatedAt(),
                user.getModifiedAt()
        ));
    }
}
