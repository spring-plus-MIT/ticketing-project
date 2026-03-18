package com.example.ticketingproject.domain.work.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkService {
    private final WorkRepository workRepository;

    public Page<WorkResponse> findAllWork(Pageable pageable) {
        Page<Work> works = workRepository.findAll(pageable);

        return works.map(WorkResponse::from);
    }

    public WorkResponse findOneWork(Long workId) {
        Work work = workRepository.findById(workId).orElseThrow(
                () -> new WorkException(
                        ErrorStatus.WORK_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.WORK_NOT_FOUND)
        );

        return WorkResponse.from(work);
    }
}
