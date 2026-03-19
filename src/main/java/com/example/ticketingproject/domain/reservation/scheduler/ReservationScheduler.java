package com.example.ticketingproject.domain.reservation.scheduler;

import com.example.ticketingproject.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationService reservationService;

    @Scheduled(cron = "0 * * * * *")
    public void processExpiredReservations() {
        log.info("만료된 결제 대기 예약 취소 스케줄러 실행");
        try {
            reservationService.cancelExpiredReservations();
        } catch (Exception e) {
            log.error("만료된 예약 취소 스케줄러 실행 중 오류 발생", e);
        }
        log.info("만료된 결제 대기 예약 취소 스케줄러 완료");
    }
}