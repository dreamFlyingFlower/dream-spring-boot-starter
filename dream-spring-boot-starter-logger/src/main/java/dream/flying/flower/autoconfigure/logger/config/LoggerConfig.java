package dream.flying.flower.autoconfigure.logger.config;

import java.util.UUID;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.Origin;

import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import lombok.RequiredArgsConstructor;

/**
 * 日志配置
 *
 * @author 飞花梦影
 * @date 2025-03-21 10:45:46
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LoggerProperties.class)
public class LoggerConfig {

	@Bean
	@API(status = Status.INTERNAL)
	CorrelationId correlationId() {
		return request -> {
			// 说明是 feign 发出的请求,不要覆盖 traceId
			if (request.getOrigin() == Origin.LOCAL) {
				return MDC.get("traceId");
			}
			String traceId = UUID.randomUUID().toString().replace("-", "");
			MDC.put("traceId", traceId);
			return traceId;
		};
	}
}