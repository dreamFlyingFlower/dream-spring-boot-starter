package dream.flying.flower.autoconfigure.logger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zalando.logbook.Sink;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

import dream.flying.flower.autoconfigure.logger.aspect.OperationLogAspect;
import dream.flying.flower.autoconfigure.logger.config.AsyncConfig;
import dream.flying.flower.autoconfigure.logger.logbook.DatabaseSink;
import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.autoconfigure.logger.service.impl.OperationLogServiceImpl;

/**
 * 操作日志自动配置类 配置日志记录所需的各个组件 包括Logbook配置、异步配置、存储配置等
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:29
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@AutoConfiguration
@Import(AsyncConfig.class)
@AutoConfigureBefore(LogbookAutoConfiguration.class)
@EnableConfigurationProperties(LoggerProperties.class)
@MapperScan("dream.flying.flower.autoconfigure.logger.mapper")
@ConditionalOnProperty(prefix = "dream.logger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OperationLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(OperationLogService.class)
	OperationLogService operationLogService() {
		return new OperationLogServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogAspect.class)
	OperationLogAspect operationLogAspect(OperationLogService operationLogService, LoggerProperties properties) {
		return new OperationLogAspect(operationLogService, properties);
	}

	@Bean
	@ConditionalOnMissingBean(DatabaseSink.class)
	@ConditionalOnProperty(prefix = "dream.logger", name = "http-log.enabled", havingValue = "true",
			matchIfMissing = true)
	Sink sink(OperationLogService operationLogService, LoggerProperties properties) {
		return new DatabaseSink(operationLogService, properties);
	}
}