package dream.flying.flower.autoconfigure.web.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dream.logger.Logger;

import dream.framework.web.service.OperateLogService;

/**
 * 日志注解切面拦截器
 *
 * @author 飞花梦影
 * @date 2022-11-12 21:13:18
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Aspect
@Component
public class LoggerAspect {

	@Autowired
	private OperateLogService operateLogService;

	/**
	 * 环绕请求切面
	 *
	 * @param joinPoint 切点
	 * @param logger 日志注解
	 */
	@Around("@annotation(logger)")
	public Object doAround(ProceedingJoinPoint joinPoint, Logger logger) {
		return operateLogService.doAroundLogger(joinPoint, logger);
	}

	/**
	 * 拦截异常操作
	 * 
	 * @param joinPoint 切点
	 * @param logger 日志注解
	 * @param e 异常
	 */
	@AfterThrowing(value = "@annotation(logger)", throwing = "e")
	public void doAfterThrowing(JoinPoint joinPoint, Logger logger, Exception e) {
		operateLogService.doAfterThrowingLogger(joinPoint, logger, e);
	}
}