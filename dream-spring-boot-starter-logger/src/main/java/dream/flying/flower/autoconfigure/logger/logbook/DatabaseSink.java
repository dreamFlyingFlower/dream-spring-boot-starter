package dream.flying.flower.autoconfigure.logger.logbook;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.lang.StrHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 将HTTP请求响应日志保存到数据库中
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:41:17
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RequiredArgsConstructor
public class DatabaseSink implements Sink {

	private final LoggerProperties loggerProperties;

	private final OperationLogService operationLogService;

	@Override
	public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
		MDC.putCloseable("traceId", precorrelation.getId());
	}

	/**
	 * 直接使用异步注解会有问题
	 * 
	 * @param correlation
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	// @Async("operationLogExecutor")
	@Override
	public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {

		try {
			OperationLogEntity operationLogEntity = OperationLogEntity.builder()
					.traceId(correlation.getId())
					.appName(loggerProperties.getAppName())
					.requestTime(LocalDateTime.now())
					.responseTime(LocalDateTime.now())
					.costTime(correlation.getDuration().toMillis())
					.requestMethod(request.getMethod())
					.requestUrl(request.getPath())
					.requestHeaders(JsonHelpers.toString(request.getHeaders()))
					.requestBody(request.getBodyAsString())
					.responseStatus(response.getStatus())
					.responseHeaders(JsonHelpers.toString(response.getHeaders()))
					.responseBody(response.getBodyAsString())
					.success(1)
					.clientIp(getIp(request))
					.createdTime(LocalDateTime.now())
					.build();

			operationLogService.save(operationLogEntity);
		} catch (Exception e) {
			log.error("Failed to save HTTP request log", e);
		}
	}

	/**
	 * 获取客户端IP
	 * 
	 * @param request 请求对象
	 * @return IP地址
	 */
	public static String getIp(HttpRequest request) {
		if (request == null) {
			return "unknown";
		}
		// 如果通过了多级反向代理,X-Forwarded-For的值并不止一个,而是一串IP值.取第一个非unknown的有效IP字符串
		String ip = request.getHeaders().getFirst("x-forwarded-for");
		if (!checkIp(ip)) {
			ip = ip.split(",")[0];
		}
		// apache http请求可能会有该值
		if (checkIp(ip)) {
			ip = request.getHeaders().getFirst("Proxy-Client-IP");
		}
		// weblogic请求可能会有该值
		if (checkIp(ip)) {
			ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
		}
		// 某些代理可能会有该值
		if (checkIp(ip)) {
			ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
		}
		// nginx可能会该值
		if (checkIp(ip)) {
			ip = request.getHeaders().getFirst("X-Real-IP");
		}
		if (checkIp(ip)) {
			ip = request.getRemote();
		}
		return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : IpHelpers.getMultistageReverseProxyIp(ip);
	}

	/**
	 * 检测给定字符串是否为未知,多用于检测HTTP请求相关
	 *
	 * @param checkString 被检测的字符串
	 * @return 是否未知
	 */
	private static boolean checkIp(String ip) {
		return StrHelper.isBlank(ip) || "unknown".equalsIgnoreCase(ip);
	}
}