package dream.flying.flower.autoconfigure.logger.aspect;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.logger.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志切面类 实现AOP切面，拦截带有@OperationLog注解的方法 记录方法的调用信息，包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:20
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OperationLogAspect {

	private final OperationLogService operationLogService;

	private final LoggerProperties properties;

	@Around("@annotation(logger)")
	public Object around(ProceedingJoinPoint point, Logger logger) throws Throwable {
		long startTime = System.currentTimeMillis();
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
			if (isNeedLog(point)) {
				saveLog(point, logger, result, success, errorMsg, System.currentTimeMillis() - startTime);
			}
		}
	}

	private boolean isNeedLog(ProceedingJoinPoint point) {
		String packageName = point.getTarget().getClass().getPackage().getName();
		return properties.getScanPackages().stream().anyMatch(packageName::startsWith);
	}

	@Async("operationLogExecutor")
	public void saveLog(ProceedingJoinPoint point, Logger logger, Object result, boolean success, String errorMsg,
			long costTime) {
		try {
			ServletRequestAttributes attributes =
					(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();

			MethodSignature signature = (MethodSignature) point.getSignature();

			OperationLogEntity operationLogEntity = OperationLogEntity.builder()
					.traceId(UUID.randomUUID().toString())
					.appName(properties.getAppName())
					.module(logger.value())
					.operationType(logger.businessType().getMsg())
					.operationDesc(logger.description())
					.methodName(signature.getMethod().getName())
					.className(point.getTarget().getClass().getName())
					.packageName(point.getTarget().getClass().getPackage().getName())
					.requestMethod(request.getMethod())
					.requestUrl(request.getRequestURI())
					.requestParams(logger.saveRequest() ? JsonHelpers.toString(point.getArgs()) : null)
					.requestBody(logger.saveRequest() ? JsonHelpers.toString(request.getParameterMap()) : null)
					.responseBody(logger.saveResponse() ? JsonHelpers.toString(result) : null)
					.success(success ? 1 : 0)
					.errorMsg(errorMsg)
					.costTime(costTime)
					.clientIp(IpHelpers.getIp(request))
					.userId(getCurrentUserId())
					.username(getCurrentUsername())
					.createdTime(LocalDateTime.now())
					.build();

			operationLogService.save(operationLogEntity);
		} catch (Exception e) {
			log.error("Failed to save operation log", e);
		}
	}

	// 这里需要根据实际项目获取当前用户信息
	private String getCurrentUserId() {
		return "system";
	}

	private String getCurrentUsername() {
		return "system";
	}
}