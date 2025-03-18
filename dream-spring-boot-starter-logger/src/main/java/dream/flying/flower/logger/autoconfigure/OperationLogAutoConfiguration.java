package dream.flying.flower.logger.autoconfigure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Sink;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.logger.aspect.OperationLogAspect;
import dream.flying.flower.logger.config.AsyncConfig;
import dream.flying.flower.logger.logbook.DatabaseSink;
import dream.flying.flower.logger.mapper.HttpRequestLogMapper;
import dream.flying.flower.logger.mapper.OperationLogMapper;
import dream.flying.flower.logger.properties.LoggerProperties;
import dream.flying.flower.logger.repository.MybatisOperationLogRepository;
import dream.flying.flower.logger.repository.OperationLogRepository;

/**
 * 操作日志自动配置类 配置日志记录所需的各个组件 包括Logbook配置、异步配置、存储配置等
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:29
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@Import(AsyncConfig.class)
@MapperScan("com.dream.logger.mapper")
@AutoConfigureAfter(LogbookAutoConfiguration.class)
@EnableConfigurationProperties(LoggerProperties.class)
@ConditionalOnProperty(prefix = "dream.logger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OperationLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogRepository.class)
	public OperationLogRepository operationLogRepository(OperationLogMapper operationLogMapper) {
		return new MybatisOperationLogRepository(operationLogMapper);
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogAspect.class)
	public OperationLogAspect operationLogAspect(OperationLogRepository logRepository, LoggerProperties properties,
			ObjectMapper objectMapper) {
		return new OperationLogAspect(logRepository, properties, objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean(DatabaseSink.class)
	@ConditionalOnProperty(prefix = "dream.logger", name = "http-log.enabled", havingValue = "true",
			matchIfMissing = true)
	public Sink databaseSink(HttpRequestLogMapper httpRequestLogMapper, LoggerProperties properties,
			ObjectMapper objectMapper) {
		return new DatabaseSink(httpRequestLogMapper, properties, objectMapper);
	}

	@Bean
	@ConditionalOnBean(DatabaseSink.class)
	@ConditionalOnMissingBean(Logbook.class)
	public Logbook logbook(ObjectMapper objectMapper, DatabaseSink databaseSink) {
		return Logbook.builder().sink(databaseSink).build();
	}
}