package dream.flying.flower.autoconfigure.logger.aspect;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
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

	private final LoggerProperties loggerProperties;

	private final OperationLogService operationLogService;

	@Around("@annotation(logger)")
	public Object around(ProceedingJoinPoint point, Logger logger) throws Throwable {
		// 设置LOG_ID,可以在logback-spring的日志格式中以%-18X{LOG_ID}的方式使用
		MDC.put(loggerProperties.getLogId(), System.currentTimeMillis() + RandomStringUtils.random(3));
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
			log.info("拦截注解Logger日志:方法调用:{},结果为:{},", success ? "成功" : "失败,原因为:" + errorMsg, result);
			operationLogService.save(point, logger, loggerProperties, result, success, errorMsg, requestTime,
					LocalDateTime.now());
		}
	}
}