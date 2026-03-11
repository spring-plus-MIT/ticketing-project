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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Transactional
public class AdminWorkServiceTest {

    @Autowired
    private AdminWorkService adminWorkService;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ObjectMapper objectMapper;

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

        // when
        WorkResponse response = adminWorkService.createWork(request);

        // then
        assertThat(response.getTitle()).isEqualTo("작품 제목");
        assertThat(response.getDescription()).isEqualTo("작품 설명");

        Work work = workRepository.findById(response.getId()).orElseThrow();

        assertThat(work.getTitle()).isEqualTo("작품 제목");
        assertThat(work.getDescription()).isEqualTo("작품 설명");
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

        workRepository.save(work);

        String json = """
                {
                    "title": "수정 제목",
                    "category": "CONCERT",
                    "description": "수정 설명"
                }
                """;


        UpdateWorkRequest request = objectMapper.readValue(json, UpdateWorkRequest.class);

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
