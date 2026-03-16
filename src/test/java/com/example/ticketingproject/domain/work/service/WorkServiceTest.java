package com.example.ticketingproject.domain.work.service;

import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkServiceTest {

    @InjectMocks
    WorkService workService;

    @Mock
    WorkRepository workRepository;

    @Test
    void 작품_전체_조회_성공_테스트() {

        // given
        List<Work> works = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Work work = Work.builder()
                    .title("작품 제목 " + i)
                    .category(Category.MUSICAL)
                    .description("작품 설명 " + i)
                    .likeCount(0L)
                    .build();

            ReflectionTestUtils.setField(work, "id", (long)i);
            works.add(work);
        }

        Pageable pageable = PageRequest.of(0, 10);

        Page<Work> workPage = new PageImpl<>(works, pageable, works.size());

        when(workRepository.findAll(pageable)).thenReturn(workPage);

        // when
        Page<WorkResponse> responses = workService.findAllWork(pageable);

        // then
        assertThat(responses.getContent().size()).isEqualTo(5);
        assertThat(responses.getContent().get(0).getTitle()).contains("작품 제목 ");
    }

    @Test
    void 작품_단건_조회_성공_테스트() {

        // given
        Work work = Work.builder()
                .title("레미제라블")
                .category(Category.MUSICAL)
                .description("노래부르면서 배 끄는거 밖에 기억이 안남")
                .likeCount(0L)
                .build();

        ReflectionTestUtils.setField(work, "id", 1L);

        when(workRepository.findById(work.getId())).thenReturn(Optional.of(work));

        // when
        WorkResponse response = workService.findOneWork(work.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("레미제라블");
    }

    @Test
    void 작품_조회_실패_테스트() {

        // given
        Work work = Work.builder()
                .title("작품 제목")
                .category(Category.MUSICAL)
                .description("작품 설명")
                .likeCount(0L)
                .build();

        ReflectionTestUtils.setField(work, "id", 2L);

        // when & then
        assertThatThrownBy(() -> workService.findOneWork(999L))
                .isInstanceOf(WorkException.class);
    }
}
