package com.example.ticketingproject.domain.like.service;


import com.example.ticketingproject.auth.exception.AuthException;
import com.example.ticketingproject.domain.like.dto.LikeResponse;
import com.example.ticketingproject.domain.like.entity.Like;
import com.example.ticketingproject.domain.like.exception.LikeException;
import com.example.ticketingproject.domain.like.repository.LikeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final LikeRepository likeRepository;

    public LikeResponse save(Long workId, Long userId) {
        Work work = workRepository.findById(workId).orElseThrow(
                () -> new WorkException(WORK_NOT_FOUND.getHttpStatus(), WORK_NOT_FOUND)
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(USER_NOT_FOUND.getHttpStatus(), USER_NOT_FOUND)
        );

        if (likeRepository.existsByUserAndWork(user.getId(), work.getId())) {
            throw new LikeException(LIKE_ALREADY_EXISTS.getHttpStatus(), LIKE_ALREADY_EXISTS);
        }

        Like like = Like.builder()
                .user(user)
                .work(work)
                .build();

        Like savedLike = likeRepository.save(like);

        workRepository.incrementLikeCount(work.getId());

        return LikeResponse.from(savedLike);
    }

    public LikeResponse delete(Long workId, Long likeId, Long userId) {
        Work work = workRepository.findById(workId).orElseThrow(
                () -> new WorkException(WORK_NOT_FOUND.getHttpStatus(), WORK_NOT_FOUND)
        );

        Like like = likeRepository.findById(likeId).orElseThrow(
                () -> new LikeException(LIKE_NOT_FOUND.getHttpStatus(), LIKE_NOT_FOUND)
        );

        if (!like.getWork().getId().equals(work.getId())) {
            throw new LikeException(LIKE_NOT_FOUND.getHttpStatus(), LIKE_NOT_FOUND);
        }

        if(!like.getUser().getId().equals(userId)) {
            throw new AuthException(ACCESS_FORBIDDEN.getHttpStatus(), ACCESS_FORBIDDEN);
        }

        likeRepository.delete(like);

        workRepository.decreaseLikeCount(work.getId());

        return LikeResponse.from(like);
    }
}
