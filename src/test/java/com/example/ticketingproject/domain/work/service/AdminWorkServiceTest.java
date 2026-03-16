package com.example.ticketingproject.domain.work.service;

import com.example.ticketingproject.domain.work.dto.CreateWorkRequest;
import com.example.ticketingproject.domain.work.dto.UpdateWorkRequest;
import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AdminWorkServiceTest {

    @InjectMocks
    private AdminWorkService adminWorkService;

    @Mock
    private WorkRepository workRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 작품_생성_성공_테스트() throws JsonProcessingException {

        // given
        String json = """
                {
                    "title": "작품 제목",
                    "category": "MUSICAL",
                    "description": "작품 설명",
                    "likeCount": 0
                }
                """;

        CreateWorkRequest request = objectMapper.readValue(json, CreateWorkRequest.class);

        Work work = Work.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .likeCount(request.getLikeCount())
                .build();

        ReflectionTestUtils.setField(work, "id", 1L);

        when(workRepository.save(any(Work.class))).thenReturn(work);

        when(workRepository.findById(work.getId())).thenReturn(Optional.of(work));

        // when
        WorkResponse response = adminWorkService.createWork(request);

        // then
        assertThat(response.getTitle()).isEqualTo("작품 제목");
        assertThat(response.getDescription()).isEqualTo("작품 설명");

        Work savedWork = workRepository.findById(response.getId()).orElseThrow();

        assertThat(savedWork.getTitle()).isEqualTo("작품 제목");
        assertThat(savedWork.getDescription()).isEqualTo("작품 설명");
    }

    @Test
    void 작품_수정_성공_테스트() throws JsonProcessingException {

        // given
        Work work = Work.builder()
                .title("제목")
                .category(Category.MUSICAL)
                .description("설명")
                .likeCount(0L)
                .build();

        ReflectionTestUtils.setField(work, "id", 2L);

        String json = """
                {
                    "title": "수정 제목",
                    "category": "CONCERT",
                    "description": "수정 설명"
                }
                """;

        UpdateWorkRequest request = objectMapper.readValue(json, UpdateWorkRequest.class);

        when(workRepository.findById(work.getId())).thenReturn(Optional.of(work));

        // when
        WorkResponse response = adminWorkService.updateWork(work.getId(), request);

        // then
        assertThat(response.getTitle()).isEqualTo("수정 제목");
        assertThat(response.getDescription()).isEqualTo("수정 설명");

        Work updated = workRepository.findById(work.getId()).orElseThrow();

        assertThat(updated.getTitle()).isEqualTo("수정 제목");
        assertThat(updated.getDescription()).isEqualTo("수정 설명");
    }
}
