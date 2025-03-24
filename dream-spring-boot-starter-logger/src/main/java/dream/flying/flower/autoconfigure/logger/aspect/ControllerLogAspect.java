package dream.flying.flower.autoconfigure.logger.aspect;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.helper.WebHelpers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 控制层日志切面类,拦截带有RestController和Controller注解的方法,记录方法的调用信息,包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2025-03-24 11:10:48
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@EnableConfigurationProperties(LoggerProperties.class)
public class ControllerLogAspect {

	private final LoggerProperties loggerProperties;

	private final OperationLogService operationLogService;

	private final ApplicationContext applicationContext;

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
	public void controllerPointcut() {
	}

	@Around("controllerPointcut()")
	public Object logHttpRequest(ProceedingJoinPoint point) throws Throwable {
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
			if (shouldLog(point)) {
				saveLog(point, result, success, errorMsg, requestTime, LocalDateTime.now());
			}
		}
	}

	private boolean shouldLog(ProceedingJoinPoint point) {
		List<String> scanPackages = loggerProperties.getScanPackages();
		if (CollectionUtils.isEmpty(scanPackages)) {
			scanPackages = AutoConfigurationPackages.get(applicationContext);
		}
		String packageName = point.getTarget().getClass().getPackage().getName();
		return scanPackages.stream().anyMatch(packageName::startsWith);
	}

	@Async("operationLogExecutor")
	public void saveLog(ProceedingJoinPoint point, Object result, boolean success, String errorMsg,
			LocalDateTime requestTime, LocalDateTime responseTime) {
		HttpServletRequest request = WebHelpers.getRequest();
		HttpServletResponse response = WebHelpers.getResponse();
		MethodSignature signature = (MethodSignature) point.getSignature();

		try {
			OperationLogEntity operationLogEntity = OperationLogEntity.builder()
					.traceId(UUID.randomUUID().toString())

					.appName(loggerProperties.getAppName())
					.module(point.getTarget().getClass().getName())
					.operationType(signature.getMethod().getName())
					.operationDesc(signature.getMethod().getName())

					.methodName(signature.getMethod().getName())
					.className(point.getTarget().getClass().getName())

					.clientIp(IpHelpers.getIp(request))
					.requestBody(JsonHelpers.toString(request.getParameterMap()))
					.requestHeaders(JsonHelpers.toString(WebHelpers.getHeaders(request)))
					.requestMethod(request.getMethod())
					.requestParams(extractParams(point))
					.requestTime(requestTime)
					.requestUrl(request.getRequestURI())

					.responseHeaders(JsonHelpers.toString(WebHelpers.getHeaders(response)))
					.responseStatus(response.getStatus())
					.responseTime(responseTime)

					.success(success ? 1 : 0)
					.errorMsg(errorMsg)
					.costTime(Duration.between(requestTime, responseTime).toMillis())
					.userId(getCurrentUserId())
					.username(getCurrentUsername())
					.createdTime(LocalDateTime.now())
					.build();

			if (success) {
				operationLogEntity.setResponseBody(JsonHelpers.toString(result));
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

	private String getCurrentUserId() {
		return "system";
	}

	private String getCurrentUsername() {
		return "system";
	}
}