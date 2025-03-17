package dream.flying.flower.logger.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import dream.flying.flower.logger.properties.LoggerProperties;
import lombok.RequiredArgsConstructor;

/**
 * 异步配置类
 * 配置异步线程池，用于异步记录操作日志
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    
    private final LoggerProperties properties;
    
    @Bean("operationLogExecutor")
    public Executor operationLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getAsyncCorePoolSize());
        executor.setMaxPoolSize(properties.getAsyncMaxPoolSize());
        executor.setQueueCapacity(properties.getAsyncQueueCapacity());
        executor.setThreadNamePrefix("operation-log-");
        executor.initialize();
        return executor;
    }
} 