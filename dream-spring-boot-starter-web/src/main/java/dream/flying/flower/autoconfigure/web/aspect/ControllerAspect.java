package dream.flying.flower.autoconfigure.web.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import dream.framework.web.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller层切面
 *
 * @author 飞花梦影
 * @date 2022-11-14 10:57:02
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class ControllerAspect {

	@Autowired
	private OperateLogService operateLogService;

	/**
	 * 拦截所有以Controller结尾的类中的所有方法
	 */
	@Pointcut("execution(* *Controller.*(..))")
	public void controllerAspectLog() {
	}

	@Around("controllerAspectLog()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		return operateLogService.doAroundController(joinPoint);
	}

	@AfterThrowing(value = "controllerAspectLog()", throwing = "throwable")
	public void doAfterThrowing(JoinPoint joinPoint, Throwable throwable) {
		log.error("###:{}", throwable.getMessage());
	}
}