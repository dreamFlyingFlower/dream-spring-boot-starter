package dream.flying.flower.autoconfigure.redis.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import dream.flying.flower.autoconfigure.redis.properties.AspectProperties;
import dream.flying.flower.framework.web.ConstWeb;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.idempotent.Idempotence;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.ResultException;

/**
 * 幂等接口切面 FIXME
 *
 * @author 飞花梦影
 * @date 2023-01-04 11:05:14
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@EnableConfigurationProperties(AspectProperties.class)
@Component
@Aspect
public class IdempotentAspect {

	@Autowired
	private Idempotence idempotence;

	@Autowired
	private AspectProperties aspectProperties;

	@Pointcut("@annotation(com.wy.idempotent.annotation.Idempotency)")
	public void idempotent() {
	}

	@Around(value = "idempotent()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		String idempontentHeaderName =
				StrHelper.getDefault(aspectProperties.getIdempontentHeaderName(), ConstWeb.HEADER_IDEMPOTENT_CODE);
		// 从header中获取幂等编码idempotentCode
		String idempotentCode = WebHelpers.getHeader(idempontentHeaderName);
		if (StrHelper.isBlank(idempotentCode)) {
			throw new ResultException("请求头中缺少" + idempontentHeaderName);
		}
		// 前置操作幂等编码是否存在
		boolean existed = idempotence.check(idempotentCode);
		if (!existed) {
			throw new ResultException("请勿重复操作");
		}
		// 删除幂等编码
		idempotence.delete(idempotentCode);
		return joinPoint.proceed();
	}

	@AfterThrowing(value = "idempotent()", throwing = "e")
	public void afterThrowing(Throwable e) {
		// 从header中获取幂等号idempotentCode
		String idempotentCode = WebHelpers.getHeader(
				StrHelper.getDefault(aspectProperties.getIdempontentHeaderName(), ConstWeb.HEADER_IDEMPOTENT_CODE));
		idempotence.record(idempotentCode, 1800L);
	}
}