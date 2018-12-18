package com.nineton.recorder.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableAsync
public class ScheduleConfig implements SchedulingConfigurer {

    private int corePoolSize = 10;
    private int maxPoolSize = 10;
    private int queueCapacity = 10;
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
//        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }

    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
    }
}
