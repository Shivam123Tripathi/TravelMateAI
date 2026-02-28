package com.travelmateai.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Configuration.
 * Enables asynchronous method execution for tasks like email sending.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool size - minimum number of threads
        executor.setCorePoolSize(2);
        
        // Max pool size - maximum number of threads
        executor.setMaxPoolSize(5);
        
        // Queue capacity - tasks waiting when all threads busy
        executor.setQueueCapacity(100);
        
        // Thread name prefix for debugging
        executor.setThreadNamePrefix("TravelMateAI-Async-");
        
        // Initialize the executor
        executor.initialize();
        
        return executor;
    }
}
