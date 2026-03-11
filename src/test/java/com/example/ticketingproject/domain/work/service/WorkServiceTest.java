package com.example.ticketingproject.domain.work.service;

import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class WorkServiceTest {

    @Autowired
    WorkService workService;

    @Autowired
    WorkRepository workRepository;

    @Test
    void 작품_전체_조회_성공_테스트() {

        // given
        for (int i = 0; i < 5; i++) {
            Work work = Work.builder()
                    .title("작품 제목 " + i)
                    .category(Category.MUSICAL)
                    .description("작품 설명 " + i)
                    .likeCount(0L)
                    .build();

            workRepository.save(work);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<WorkResponse> works = workService.findAllWork(pageable);

        // then
        assertThat(works.getContent().size()).isEqualTo(5);
        assertThat(works.getContent().get(0).getTitle()).contains("작품 제목 ");
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

        workRepository.save(work);

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

        // when & then
        assertThatThrownBy(() -> workService.findOneWork(999L))
                .isInstanceOf(WorkException.class);
    }
}
