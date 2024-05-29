package dream.flying.flower.autoconfigure.web.aspect;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 利用AOP加ReentrantLock锁
 *
 * @author 飞花梦影
 * @date 2024-05-29 09:55:39
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@Scope
@Aspect
@Order(1)
public class LockAspect {

	private final static Lock LOCK = new ReentrantLock(true);

	@Pointcut("@annotation(dream.framework.core.annotation.MethodLock)")
	public void lockAspect() {

	}

	@Around("lockAspect()")
	public Object around(ProceedingJoinPoint joinPoint) {
		LOCK.lock();
		Object obj = null;
		try {
			obj = joinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("MethodLock切面调用失败!");
		} finally {
			LOCK.unlock();
		}
		return obj;
	}
}