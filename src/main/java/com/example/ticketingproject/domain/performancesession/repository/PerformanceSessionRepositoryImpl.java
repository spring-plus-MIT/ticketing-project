package com.example.ticketingproject.domain.performancesession.repository;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.performance.entity.QPerformance;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.entity.QPerformanceSession;
import com.example.ticketingproject.domain.work.entity.QWork;
import com.example.ticketingproject.domain.work.enums.Category;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PerformanceSessionRepositoryImpl implements PerformanceSessionCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QPerformanceSession performanceSession = QPerformanceSession.performanceSession;
    private final QPerformance performance = QPerformance.performance;
    private final QWork work = QWork.work;


    @Override
    public Page<GetSessionResponse> searchSessions(String keyword, Category category, LocalDateTime startTime, LocalDateTime endTime, PerformanceStatus status, Pageable converted) {
        List<GetSessionResponse> result = queryFactory
                .select(Projections.constructor(GetSessionResponse.class,
                        performanceSession.id,
                        work.title,
                        performanceSession.venue.name,
                        performanceSession.startTime,
                        performanceSession.endTime
                        ))
                .from(performanceSession)
                .join(performanceSession.performance, performance)
                .join(performance.work, work)
                .where(
                        titleLike(keyword),
                        categoryEq(category),
                        dateBetween(startTime, endTime),
                        statusEq(status)
                )
                .offset(converted.getOffset())
                .limit(converted.getPageSize())
                .orderBy(performanceSession.startTime.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(performanceSession.count())
                .from(performanceSession)
                .join(performanceSession.performance, performance)
                .join(performance.work, work)
                .where(
                        titleLike(keyword),
                        categoryEq(category),
                        dateBetween(startTime, endTime),
                        statusEq(status)
                );

        return PageableExecutionUtils.getPage(result, converted, countQuery::fetchOne);
    }

    private BooleanExpression titleLike(String keyword) {
        return StringUtils.hasText(keyword)
                ? work.title.containsIgnoreCase(keyword)
                : null;
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null
                ? work.category.eq(category)
                : null;
    }

    private BooleanExpression dateBetween(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null && endTime == null) return null;
        if (startTime == null) return performanceSession.startTime.loe(endTime);
        if (endTime == null) return performanceSession.endTime.goe(startTime);

        return performanceSession.startTime.loe(endTime)
                .and(performanceSession.endTime.goe(startTime));
    }

    private BooleanExpression statusEq(PerformanceStatus status) {
        return status != null
                ? performance.status.eq(status)
                : null;
    }
}
