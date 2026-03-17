package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.enums.SeatStatus;
import com.example.ticketingproject.domain.seat.exception.SeatException;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.exception.VenueException;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.redis.lock.annotation.RedisLock;
import com.example.ticketingproject.redis.lock.enums.LockStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
public class AdminSeatService {

    private final SeatTransactionalService seatTransactionalService;

    // SpEL 표현식으로 Lock Key 동적 생성
    // SpEL 작성법 = 문자열은 ' '로 감싸고, 메서드 파라미터는 #파라미터명
    // "'Key 시작 이름' + #파라미터값 변수명(추가로 붙일 키 이름은 '' 감싸서 추가)"
    // 예) venueId = 2 -> Lock:venue:2:seat:create
    @RedisLock(key = "'lock:venue:' + #venueId + ':seat:create'", strategy = LockStrategy.RETRY)
    public SeatResponse saveRedisLock(Long venueId, CreateSeatRequest request) {
        Seat seat = seatTransactionalService.saveSeat(venueId, request);
        return SeatResponse.from(seat);
    }

    // 낙관적 Lock 적용 메서드 (Retry 전략 위한 Transactional 분리)
    public SeatResponse saveOptimisticLock(Long venueId, CreateSeatRequest request) {
        int retry = 3;

        while (retry > 0) {
            try {
                Seat seat = seatTransactionalService.saveSeat(venueId, request);
                seatTransactionalService.venueTouch(seat.getVenue());
                return SeatResponse.from(seat);

            } catch (ObjectOptimisticLockingFailureException e) {
                retry--;

                if (retry == 0) {
                    throw new SeatException(
                            ErrorStatus.DUPLICATE_SEAT.getHttpStatus(),
                            ErrorStatus.DUPLICATE_SEAT
                    );
                }

                try {
                    // 재시도 횟수에 따라 대기 시간 증가 (exponential backoff)
                    long sleepTime = 50L * (4 - retry);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {

                }
            }
        }

        throw new SeatException(
                ErrorStatus.DUPLICATE_SEAT.getHttpStatus(),
                ErrorStatus.DUPLICATE_SEAT
        );
    }
}
