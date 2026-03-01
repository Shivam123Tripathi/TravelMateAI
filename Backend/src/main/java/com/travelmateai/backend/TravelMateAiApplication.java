package com.travelmateai.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TravelMate AI Application - Main Entry Point
 * 
 * A production-ready REST API backend for a smart travel assistant platform.
 * 
 * Features:
 * - User authentication with JWT
 * - Trip management
 * - Booking system with email notifications
 * - Seat locking system with automatic expiration
 * - Admin reporting and analytics
 * 
 * @author TravelMate AI Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@Slf4j
public class TravelMateAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelMateAiApplication.class, args);
        log.info("========================================");
        log.info("  TravelMate AI Backend Started!");
        log.info("  Base URL: http://localhost:8080");
        log.info("  API Base: http://localhost:8080/api");
        log.info("========================================");
    }
}
