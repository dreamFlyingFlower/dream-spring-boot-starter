package dream.flying.flower.autoconfigure.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.wy.idempotent.Idempotence;
import com.wy.lang.StrHelper;
import com.wy.result.ResultException;

import dream.flying.flower.autoconfigure.web.properties.AopProperties;
import dream.framework.web.ConstWeb;
import dream.framework.web.helper.WebHelpers;

/**
 * 幂等接口切面 FIXME
 *
 * @author 飞花梦影
 * @date 2023-01-04 11:05:14
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@EnableConfigurationProperties(AopProperties.class)
@Component
@Aspect
public class IdempotentAspect {

	@Autowired
	private Idempotence idempotence;

	@Autowired
	private AopProperties aopProperties;

	@Pointcut("@annotation(com.wy.idempotent.annotation.Idempotency)")
	public void idempotent() {
	}

	@Around(value = "idempotent()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		String idempontentHeaderName =
				StrHelper.getDefault(aopProperties.getIdempontentHeaderName(), ConstWeb.HEADER_IDEMPOTENT_CODE);
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
				StrHelper.getDefault(aopProperties.getIdempontentHeaderName(), ConstWeb.HEADER_IDEMPOTENT_CODE));
		idempotence.record(idempotentCode, 1800L);
	}
}