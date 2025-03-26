package dream.flying.flower.autoconfigure.redis.handler;

import javax.servlet.http.HttpServletRequest;

import dream.flying.flower.autoconfigure.redis.helper.RedisHelpers;
import dream.flying.flower.autoconfigure.redis.properties.TokenProperties;
import dream.flying.flower.digest.DigestHelper;
import dream.flying.flower.enums.TipEnum;
import dream.flying.flower.framework.web.handler.TokenHandler;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.ResultException;
import lombok.AllArgsConstructor;

/**
 * Token业务实现类
 *
 * @author 飞花梦影
 * @date 2021-11-09 16:21:50
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AllArgsConstructor
public class RedisTokenHandler implements TokenHandler {

	private final RedisHelpers redisHelpers;

	private final TokenProperties tokenProperties;

	@Override
	public String createToken() {
		StringBuilder builder = new StringBuilder();
		builder.append(tokenProperties.getTokenPrefix()).append(DigestHelper.uuid());
		redisHelpers.setNX(builder.toString(), builder.toString(), 10000L);
		return builder.toString();
	}

	@Override
	public boolean checkToken(HttpServletRequest request, String tokenKey) {
		String token = request.getHeader(tokenKey);
		if (StrHelper.isBlank(token)) {
			token = request.getParameter(tokenKey);
			if (StrHelper.isBlank(token)) {
				throw new ResultException(TipEnum.TIP_AUTH_TOKEN_EMPTY);
			}
		}

		if (!redisHelpers.exist(tokenKey)) {
			throw new ResultException(TipEnum.TIP_AUTH_TOKEN_NOT_EXIST);
		}
		boolean success = redisHelpers.atomicCompareAndDelete(tokenKey, token);
		if (!success) {
			throw new ResultException(TipEnum.TIP_AUTH_TOKEN_NOT_EXIST);
		}
		return true;
	}

	@Override
	public String getToken(HttpServletRequest request, String tokenKey) {
		String token = request.getHeader(tokenKey);
		if (StrHelper.isBlank(token)) {
			token = request.getParameter(tokenKey);
			if (StrHelper.isBlank(token)) {
				throw new ResultException(TipEnum.TIP_AUTH_TOKEN_EMPTY);
			}
		}
		return token;
	}
}