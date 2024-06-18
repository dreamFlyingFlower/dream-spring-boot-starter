package dream.flying.flower.autoconfigure.redis.idempotent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import dream.flying.flower.autoconfigure.redis.helper.RedisHelpers;
import dream.flying.flower.idempotent.Idempotence;
import lombok.AllArgsConstructor;

/**
 * 使用Redis实现幂等
 *
 * @author 飞花梦影
 * @date 2023-01-04 10:31:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ConditionalOnBean(value = { RedisTemplate.class }, name = "redisTemplate")
@Configuration
@AllArgsConstructor
public class RedisIdempotence implements Idempotence {

	private final RedisHelpers redisHelper;

	@Override
	public boolean check(String idempotentCode) {
		return redisHelper.exist(idempotentCode);
	}

	@Override
	public void record(String idempotentCode) {
		redisHelper.set(idempotentCode, "1");
	}

	@Override
	public void record(String idempotentCode, Long time) {
		redisHelper.setExpire(idempotentCode, "1", time);
	}

	@Override
	public void delete(String idempotentCode) {
		redisHelper.clear(idempotentCode);
	}
}