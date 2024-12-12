package com.continuum.cms.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    private final CMSServiceConfig config;

    public AsyncConfig(CMSServiceConfig config) {
        this.config = config;
    }
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getNumberOfThreads());
        executor.setMaxPoolSize(config.getMaxNumberOfThreads());
        executor.setQueueCapacity(config.getMaxQueueSize());
        executor.setThreadNamePrefix(config.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
