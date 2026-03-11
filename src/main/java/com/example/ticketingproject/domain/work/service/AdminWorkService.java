package com.example.ticketingproject.domain.work.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.work.dto.CreateWorkRequest;
import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.dto.UpdateWorkRequest;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminWorkService {
    private final WorkRepository workRepository;

    @Transactional
    public WorkResponse createWork(CreateWorkRequest request) {
        Work work = Work.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .likeCount(request.getLikeCount())
                .build();

        Work savedWork = workRepository.save(work);

        return WorkResponse.from(savedWork);
    }

    @Transactional
    public WorkResponse updateWork(Long workId, UpdateWorkRequest request) {
        Work work = workRepository.findById(workId).orElseThrow(
                () -> new WorkException(
                        ErrorStatus.WORK_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.WORK_NOT_FOUND
                )
        );

        work.update(
                request.getTitle(),
                request.getCategory(),
                request.getDescription()
        );

        return WorkResponse.from(work);
    }
}
