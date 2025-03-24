package dream.flying.flower.autoconfigure.logger.aspect;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Async;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.logger.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志切面类.拦截带有Logger注解的方法,记录方法的调用信息,包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:20
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@EnableConfigurationProperties(LoggerProperties.class)
public class OperationLogAspect {

	private final LoggerProperties properties;

	private final OperationLogService operationLogService;

	@Around("@annotation(logger)")
	public Object around(ProceedingJoinPoint point, Logger logger) throws Throwable {
		LocalDateTime requestTime = LocalDateTime.now();
		Object result = null;
		boolean success = false;
		String errorMsg = null;

		try {
			result = point.proceed();
			success = true;
			return result;
		} catch (Exception e) {
			errorMsg = e.getMessage();
			throw e;
		} finally {
			saveLog(point, logger, result, success, errorMsg, requestTime, LocalDateTime.now());
		}
	}

	@Async("operationLogExecutor")
	public void saveLog(ProceedingJoinPoint point, Logger logger, Object result, boolean success, String errorMsg,
			LocalDateTime requestTime, LocalDateTime responseTime) {
		HttpServletRequest request = WebHelpers.getRequest();
		HttpServletResponse response = WebHelpers.getResponse();
		MethodSignature signature = (MethodSignature) point.getSignature();

		try {
			OperationLogEntity operationLogEntity = OperationLogEntity.builder()
					.traceId(UUID.randomUUID().toString())
					.appName(properties.getAppName())
					.module(logger.value())
					.operationType(logger.businessType().getMsg())
					.operationDesc(logger.description())
					.methodName(signature.getMethod().getName())
					.className(point.getTarget().getClass().getName())

					.clientIp(IpHelpers.getIp(request))
					.requestBody(logger.saveRequest() ? JsonHelpers.toString(request.getParameterMap()) : null)
					.requestHeaders(JsonHelpers.toString(WebHelpers.getHeaders(request)))
					.requestMethod(request.getMethod())
					.requestParams(logger.saveRequest() ? extractParams(point) : null)
					.requestTime(requestTime)
					.requestUrl(request.getRequestURI())

					.responseHeaders(JsonHelpers.toString(WebHelpers.getHeaders(response)))
					.responseStatus(response.getStatus())
					.responseTime(responseTime)

					.success(success ? 1 : 0)
					.errorMsg(errorMsg)
					.costTime(Duration.between(requestTime, responseTime).get(ChronoUnit.MILLIS))
					.userId(getCurrentUserId())
					.username(getCurrentUsername())
					.createdTime(LocalDateTime.now())
					.build();

			if (success) {
				operationLogEntity.setResponseBody(logger.saveResponse() ? JsonHelpers.toString(result) : null);
			} else {
				operationLogEntity.setResponseBody(errorMsg);
			}

			operationLogService.save(operationLogEntity);
		} catch (Exception e) {
			log.error("Failed to save operation log", e);
		}
	}

	/**
	 * 获取参数名和参数值,WebHelpers里有相同方法,看看各种方法的优劣
	 *
	 * @param joinPoint
	 * @return 参数
	 */
	public String extractParams(ProceedingJoinPoint joinPoint) {
		Map<String, Object> params = new HashMap<>();
		Object[] values = joinPoint.getArgs();
		String[] names = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
		for (int i = 0; i < names.length; i++) {
			params.put(names[i], values[i]);
		}
		return JsonHelpers.toString(params);
	}

	// 这里需要根据实际项目获取当前用户信息
	private String getCurrentUserId() {
		return "system";
	}

	private String getCurrentUsername() {
		return "system";
	}
}