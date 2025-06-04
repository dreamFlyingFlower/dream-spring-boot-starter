package dream.flying.flower.autoconfigure.logger.aspect;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
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

	private final ApplicationContext applicationContext;

	private final OperationLogService operationLogService;

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
	public void controllerPointcut() {
	}

	@Around("controllerPointcut()")
	public Object logHttpRequest(ProceedingJoinPoint point) throws Throwable {
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
			log.info("拦截RestController,Controller日志:方法调用:{},结果为:{},", success ? "成功" : "失败,原因为:" + errorMsg, result);
			if (shouldLog(point)) {
				operationLogService.save(point, loggerProperties, result, success, errorMsg, requestTime,
						LocalDateTime.now());
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
}