package dream.flying.flower.logger.aspect;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.logger.Logger;
import dream.flying.flower.logger.model.OperationLogModel;
import dream.flying.flower.logger.properties.LoggerProperties;
import dream.flying.flower.logger.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志切面类 实现AOP切面，拦截带有@OperationLog注解的方法 记录方法的调用信息，包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OperationLogAspect {

	private final OperationLogRepository logRepository;

	private final LoggerProperties properties;

	private final ObjectMapper objectMapper;

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

			OperationLogModel logModel = OperationLogModel.builder()
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
					.requestParams(logger.saveRequest() ? objectMapper.writeValueAsString(point.getArgs()) : null)
					.requestBody(
							logger.saveRequest() ? objectMapper.writeValueAsString(request.getParameterMap()) : null)
					.responseBody(logger.saveResponse() ? objectMapper.writeValueAsString(result) : null)
					.success(success)
					.errorMsg(errorMsg)
					.costTime(costTime)
					.clientIp(getClientIp(request))
					.userId(getCurrentUserId())
					.username(getCurrentUsername())
					.createdTime(LocalDateTime.now())
					.build();

			logRepository.save(logModel);
		} catch (Exception e) {
			log.error("Failed to save operation log", e);
		}
	}

	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	// 这里需要根据实际项目获取当前用户信息
	private String getCurrentUserId() {
		return "system";
	}

	private String getCurrentUsername() {
		return "system";
	}
}