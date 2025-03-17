package dream.flying.flower.logger.logbook;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.logger.entity.HttpRequestLog;
import dream.flying.flower.logger.mapper.HttpRequestLogMapper;
import dream.flying.flower.logger.properties.LoggerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据库HTTP日志记录器 将HTTP请求响应日志保存到数据库中
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DatabaseHttpLogWriter implements HttpLogWriter {

	private final HttpRequestLogMapper logMapper;

	private final LoggerProperties properties;

	private final ObjectMapper objectMapper;

	@Async("operationLogExecutor")
	@Override
	public void write(final Precorrelation precorrelation, final String request) throws IOException {
		// 暂存请求信息，等响应回来一起保存

	}

	@Override
	public void write(final Correlation correlation, final String response) throws IOException {
		try {
			HttpRequestLog logEntity = HttpRequestLog.builder()
					.traceId(UUID.randomUUID().toString())
					.appName(properties.getAppName())
					.requestTime(LocalDateTime.now())
					.responseTime(LocalDateTime.now())
					.costTime(correlation.getDuration().toMillis())
					.requestMethod(correlation.getRequest().getMethod())
					.requestUrl(correlation.getRequest().getPath())
					.requestHeaders(objectMapper.writeValueAsString(correlation.getRequest().getHeaders()))
					.requestBody(correlation.getRequest().getBodyAsString())
					.responseStatus(correlation.getResponse().getStatus())
					.responseHeaders(objectMapper.writeValueAsString(correlation.getResponse().getHeaders()))
					.responseBody(correlation.getResponse().getBodyAsString())
					.clientIp(getClientIp(correlation))
					.createdTime(LocalDateTime.now())
					.build();

			logMapper.insert(logEntity);
		} catch (Exception e) {
			log.error("Failed to save HTTP request log", e);
		}
	}

	private String getClientIp(Correlation correlation) {
		String ip = correlation.getRequest().getHeaders().get("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = correlation.getRequest().getHeaders().get("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = correlation.getRequest().getHeaders().get("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = correlation.getRequest().getRemote();
		}
		return ip;
	}
}