package dream.flying.flower.logger.logbook;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.framework.core.helper.IpHelpers;
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
public class DatabaseHttpLogWriter implements Sink {

	private final HttpRequestLogMapper logMapper;

	private final LoggerProperties properties;

	private final ObjectMapper objectMapper;

	@Override
	public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {

	}

	@Async("operationLogExecutor")
	@Override
	public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {
		try {
			HttpRequestLog logEntity = HttpRequestLog.builder()
					.traceId(UUID.randomUUID().toString())
					.appName(properties.getAppName())
					.requestTime(LocalDateTime.now())
					.responseTime(LocalDateTime.now())
					.costTime(correlation.getDuration().toMillis())
					.requestMethod(request.getMethod())
					.requestUrl(request.getPath())
					.requestHeaders(objectMapper.writeValueAsString(request.getHeaders()))
					.requestBody(request.getBodyAsString())
					.responseStatus(response.getStatus())
					.responseHeaders(objectMapper.writeValueAsString(response.getHeaders()))
					.responseBody(response.getBodyAsString())
					.clientIp(IpHelpers.getIp(request))
					.createdTime(LocalDateTime.now())
					.build();

			logMapper.insert(logEntity);
		} catch (Exception e) {
			log.error("Failed to save HTTP request log", e);
		}
	}
}