package com.travelmateai.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * TravelMate AI Application - Main Entry Point
 * 
 * A production-ready REST API backend for a smart travel assistant platform.
 * 
 * Features:
 * - User authentication with JWT
 * - Trip management
 * - Booking system with email notifications
 * - Admin reporting and analytics
 * 
 * @author TravelMate AI Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@Slf4j
public class TravelMateAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelMateAiApplication.class, args);
        log.info("========================================");
        log.info("  TravelMate AI Backend Started!");
        log.info("  Swagger UI: http://localhost:8080/swagger-ui.html");
        log.info("  API Docs:   http://localhost:8080/api-docs");
        log.info("========================================");
    }
}
