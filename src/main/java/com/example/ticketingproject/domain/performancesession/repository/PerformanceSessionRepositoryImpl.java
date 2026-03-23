package com.example.ticketingproject.domain.performancesession.repository;

import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.entity.QPerformance;
import com.example.ticketingproject.domain.performancesession.entity.QPerformanceSession;
import com.example.ticketingproject.domain.work.entity.QWork;
import com.example.ticketingproject.domain.work.enums.Category;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class PerformanceSessionRepositoryImpl implements PerformanceSessionCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QPerformanceSession performanceSession =  QPerformanceSession.performanceSession;
    private final QPerformance performance = QPerformance.performance;
    private final QWork work = QWork.work;


    @Override
    public Page<PerformanceSearchResponse> searchPerformance(String keyword, Category category, LocalDate startDate, LocalDate endDate, PerformanceStatus status, Pageable converted) {
        List<PerformanceSearchResponse> result = queryFactory
                .select(Projections.constructor(PerformanceSearchResponse.class,
                        performance.id,
                        performance.season,
                        work.category,
                        performance.status
                        ))
                .from(performanceSession)
                .join(performanceSession.performance, performance)
                .join(performance.work, work)
                .where(
                        titleLike(keyword),
                        categoryEq(category),
                        dateBetween(startDate, endDate),
                        statusEq(status)
                )
                .groupBy(performance.id, performance.season, work.category, performance.status)
                .offset(converted.getOffset())
                .limit(converted.getPageSize())
                .orderBy(performance.startDate.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(performance.countDistinct())
                .from(performanceSession)
                .join(performanceSession.performance, performance)
                .join(performance.work, work)
                .where(
                        titleLike(keyword),
                        categoryEq(category),
                        dateBetween(startDate, endDate),
                        statusEq(status)
                );

        return PageableExecutionUtils.getPage(result, converted, countQuery::fetchOne);
    }

    @Override
    public List<PerformanceSearchResponse> searchPerformanceContent(String keyword, Category category, LocalDate startDate, LocalDate endDate, PerformanceStatus status, Pageable converted) {
        List<PerformanceSearchResponse> result = queryFactory
                .select(Projections.constructor(PerformanceSearchResponse.class,
                        performance.id,
                        performance.season,
                        work.category,
                        performance.status
                ))
                .from(performanceSession)
                .join(performanceSession.performance, performance)
                .join(performance.work, work)
                .where(
                        titleLike(keyword),
                        categoryEq(category),
                        dateBetween(startDate, endDate),
                        statusEq(status)
                )
                .groupBy(performance.id, performance.season, work.category, performance.status)
                .offset(converted.getOffset())
                .limit(converted.getPageSize())
                .orderBy(performance.startDate.asc())
                .fetch();

        return result;
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

    private BooleanExpression dateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) return null;
        if (startDate == null) return performanceSession.startTime.loe(endDate.atTime(23, 59, 59));
        if (endDate == null) return performanceSession.startTime.goe(startDate.atStartOfDay());

        return performanceSession.startTime.goe(startDate.atStartOfDay())
                .and(performanceSession.startTime.loe(endDate.atTime(23, 59, 59)));
    }

    private BooleanExpression statusEq(PerformanceStatus status) {
        return status != null
                ? performance.status.eq(status)
                : null;
    }

    @Override
    public long countPerformance(String keyword, Category category, LocalDate startDate, LocalDate endDate, PerformanceStatus status) {
        Long count = queryFactory
                .select(performance.countDistinct())
                .from(performanceSession)
                .join(performanceSession.performance, performance)
                .join(performance.work, work)
                .where(
                        titleLike(keyword),
                        categoryEq(category),
                        dateBetween(startDate, endDate),
                        statusEq(status)
                )
                .fetchOne();
        return count != null ? count : 0L;
    }
}
