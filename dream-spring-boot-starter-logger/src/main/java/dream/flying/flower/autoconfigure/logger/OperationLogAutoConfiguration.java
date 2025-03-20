package dream.flying.flower.autoconfigure.logger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
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
import dream.flying.flower.framework.core.constant.ConstConfigPrefix;

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
@MapperScan("dream.flying.flower.autoconfigure.logger.mapper")
@EnableConfigurationProperties({ LoggerProperties.class, FlywayProperties.class })
@AutoConfigureBefore({ LogbookAutoConfiguration.class, FlywayAutoConfiguration.class })
@ConditionalOnProperty(prefix = ConstConfigPrefix.AUTO_LOGGER, name = ConstConfigPrefix.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class OperationLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(OperationLogService.class)
	OperationLogService operationLogService(FlywayProperties flywayProperties) {
		// 防止生产环境误操作清除所有表,必须设置为true
		flywayProperties.setCleanDisabled(true);
		// 首次迁移时基线化非空数据库
		flywayProperties.setBaselineOnMigrate(true);
		return new OperationLogServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogAspect.class)
	OperationLogAspect operationLogAspect(LoggerProperties loggerProperties, OperationLogService operationLogService) {
		return new OperationLogAspect(loggerProperties, operationLogService);
	}

	@Bean
	@ConditionalOnMissingBean(DatabaseSink.class)
	Sink sink(LoggerProperties loggerProperties, OperationLogService operationLogService) {
		return new DatabaseSink(loggerProperties, operationLogService);
	}
}