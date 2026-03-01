package com.travelmateai.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job to automatically release expired seat locks.
 * Runs at a configurable rate (default: every 60 seconds) defined in application.yml.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SeatLockScheduler {

    private final SeatLockService seatLockService;

    /**
     * Release expired seat locks.
     * Rate is configurable via seat.lock.schedule-rate-ms in application.yml.
     */
    @Scheduled(fixedRateString = "${seat.lock.schedule-rate-ms:60000}")
    public void releaseExpiredLocksJob() {
        try {
            log.debug("Starting scheduled job to release expired seat locks");
            seatLockService.releaseExpiredLocks();
            log.debug("Completed scheduled job to release expired seat locks");
        } catch (Exception e) {
            log.error("Error in scheduled job for releasing expired seat locks", e);
        }
    }
}
