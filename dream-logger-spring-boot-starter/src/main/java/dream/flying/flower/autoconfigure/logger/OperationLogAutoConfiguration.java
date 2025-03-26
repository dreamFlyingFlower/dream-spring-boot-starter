package dream.flying.flower.autoconfigure.logger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.zalando.logbook.DefaultHttpLogFormatter;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Sink;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

import dream.flying.flower.autoconfigure.logger.aspect.ControllerLogAspect;
import dream.flying.flower.autoconfigure.logger.aspect.OperationLogAspect;
import dream.flying.flower.autoconfigure.logger.config.AsyncConfig;
import dream.flying.flower.autoconfigure.logger.processor.FlywayPropertiesBeanProcessor;
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
@EnableConfigurationProperties({ LoggerProperties.class })
@AutoConfiguration(before = { LogbookAutoConfiguration.class })
@MapperScan("dream.flying.flower.autoconfigure.logger.mapper")
@Import({ AsyncConfig.class, FlywayPropertiesBeanProcessor.class })
@ConditionalOnProperty(prefix = ConstConfigPrefix.AUTO_LOGGER, name = ConstConfigPrefix.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class OperationLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(OperationLogService.class)
	OperationLogService operationLogService() {
		return new OperationLogServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogAspect.class)
	OperationLogAspect operationLogAspect(LoggerProperties loggerProperties, OperationLogService operationLogService) {
		return new OperationLogAspect(loggerProperties, operationLogService);
	}

	@Bean
	@ConditionalOnMissingBean(ControllerLogAspect.class)
	ControllerLogAspect controllerLogAspect(LoggerProperties loggerProperties, OperationLogService operationLogService,
			ApplicationContext applicationContext) {
		return new ControllerLogAspect(loggerProperties, operationLogService, applicationContext);
	}

	@Bean
	@ConditionalOnMissingBean(Sink.class)
	Sink sink(Environment environment, LoggingSystem loggingSystem) {
		// 设置logbook日志级别
		if (!environment.containsProperty("logging.level.org.zalando.logbook")) {
			loggingSystem.setLogLevel("org.zalando.logbook", LogLevel.TRACE);
		}
		return new DefaultSink(new DefaultHttpLogFormatter(), new DefaultHttpLogWriter());
	}
}