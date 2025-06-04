package dream.flying.flower.autoconfigure.logger.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.mapper.OperationLogMapper;
import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.logger.Logger;
import lombok.RequiredArgsConstructor;

/**
 * 操作日志MyBatis存储实现类 实现日志的持久化存储，使用MyBatis-Plus进行数据库操作
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 */
@EnableAsync
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogEntity>
		implements OperationLogService {

	@Override
	@Async("operationLogExecutor")
	public void save(ProceedingJoinPoint point, LoggerProperties loggerProperties, Object result, boolean success,
			String errorMsg, LocalDateTime requestTime, LocalDateTime responseTime) {
		HttpServletRequest request = WebHelpers.getRequest();
		HttpServletResponse response = WebHelpers.getResponse();
		MethodSignature signature = (MethodSignature) point.getSignature();

		try {
			OperationLogEntity operationLogEntity = OperationLogEntity.builder()
					.traceId(MDC.get(loggerProperties.getLogId()))

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
					.requestParams(extractParams(point, loggerProperties))
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

			save(operationLogEntity);
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
	protected String extractParams(ProceedingJoinPoint joinPoint, LoggerProperties loggerProperties) {
		Map<String, Object> params = new HashMap<>();
		Object[] values = joinPoint.getArgs();
		String[] names = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
		for (int i = 0; i < names.length; i++) {
			if (excludeParameterType(loggerProperties, values[i])) {
				continue;
			}
			params.put(names[i], values[i]);
		}
		return JsonHelpers.toString(params, loggerProperties.getExcludeParameterNames());
	}

	protected boolean excludeParameterType(LoggerProperties loggerProperties, Object object) {
		return CollectionUtils.isEmpty(loggerProperties.getExcludeParameterTypes())
				? false
				: loggerProperties.getExcludeParameterTypes().stream().anyMatch(object.getClass()::isAssignableFrom);
	}

	protected String getCurrentUserId() {
		return "guest";
	}

	protected String getCurrentUsername() {
		return "guest";
	}

	@Override
	@Async("operationLogExecutor")
	public void save(ProceedingJoinPoint point, Logger logger, LoggerProperties loggerProperties, Object result,
			boolean success, String errorMsg, LocalDateTime requestTime, LocalDateTime responseTime) {
		HttpServletRequest request = WebHelpers.getRequest();
		HttpServletResponse response = WebHelpers.getResponse();
		MethodSignature signature = (MethodSignature) point.getSignature();

		try {
			OperationLogEntity operationLogEntity = OperationLogEntity.builder()
					.traceId(MDC.get(loggerProperties.getLogId()))
					.appName(loggerProperties.getAppName())
					.module(logger.value())
					.operationType(logger.businessType().getMsg())
					.operationDesc(logger.description())
					.methodName(signature.getMethod().getName())
					.className(point.getTarget().getClass().getName())

					.clientIp(IpHelpers.getIp(request))
					.requestBody(logger.saveRequest() ? JsonHelpers.toString(request.getParameterMap()) : null)
					.requestHeaders(JsonHelpers.toString(WebHelpers.getHeaders(request)))
					.requestMethod(request.getMethod())
					.requestParams(logger.saveRequest() ? extractParams(point, loggerProperties) : null)
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

			save(operationLogEntity);
		} catch (Exception e) {
			log.error("Failed to save operation log", e);
		}
	}
}