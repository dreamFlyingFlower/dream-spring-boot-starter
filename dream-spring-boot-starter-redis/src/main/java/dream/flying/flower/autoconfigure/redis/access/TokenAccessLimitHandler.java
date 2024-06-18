package dream.flying.flower.autoconfigure.redis.access;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dream.flying.flower.autoconfigure.redis.helper.RedisHelpers;
import dream.flying.flower.autoconfigure.redis.properties.TokenProperties;
import dream.flying.flower.framework.web.handler.TokenService;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.limit.LimitAccessHandler;
import dream.flying.flower.limit.annotation.LimitAccess;
import dream.flying.flower.result.ResultException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用token进行访问次数限制
 *
 * @author 飞花梦影
 * @date 2022-06-23 09:43:09
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@AllArgsConstructor
public class TokenAccessLimitHandler implements LimitAccessHandler {

	private final RedisHelpers redisHelper;

	private final TokenService tokenService;

	private final TokenProperties tokenProperties;

	@Override
	public boolean handler(LimitAccess limitAccess) {
		ServletRequestAttributes servletRequestAttributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String token = tokenService.getToken(request, tokenProperties.getTokenLogin());
		if (StrHelper.isBlank(token)) {
			throw new ResultException("token不存在,请检查!");
		}

		int loginNum = 0;
		Object count = redisHelper.get(token);
		if (Objects.isNull(count)) {
			loginNum++;
		} else {
			loginNum = Integer.parseInt(count.toString());
		}

		if (loginNum > limitAccess.count()) {
			log.error("访问频繁,请稍后重试");
			return false;
		}
		if (loginNum <= limitAccess.count()) {
			loginNum++;
		}
		redisHelper.set(token, loginNum);
		return true;
	}
}