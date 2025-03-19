package dream.flying.flower.autoconfigure.logger.config;

import java.util.concurrent.Executor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import lombok.RequiredArgsConstructor;

/**
 * 异步配置类 配置异步线程池,用于异步记录操作日志
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableAsync
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LoggerProperties.class)
public class AsyncConfig {

	private final LoggerProperties properties;

	@Bean("operationLogExecutor")
	Executor operationLogExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(properties.getAsyncCorePoolSize());
		executor.setMaxPoolSize(properties.getAsyncMaxPoolSize());
		executor.setQueueCapacity(properties.getAsyncQueueCapacity());
		executor.setThreadNamePrefix("operation-log-");
		executor.initialize();
		return executor;
	}
}