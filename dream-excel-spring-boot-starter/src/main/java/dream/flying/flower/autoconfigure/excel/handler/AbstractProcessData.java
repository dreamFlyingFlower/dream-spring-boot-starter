package dream.flying.flower.autoconfigure.excel.handler;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-17 19:47:07
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public abstract class AbstractProcessData<T> implements ProcessData<T>, ParamHandler {

	@Resource
	private RedisTemplate<String, T> redisTemplate;

	public final int expireSecond = 600;

	public void saveTemp(Set<ZSetOperations.TypedTuple<T>> list) {
		this.redisTemplate.opsForZSet().add(getUqKey(), list);
		this.redisTemplate.expire(getUqKey(), 600L, TimeUnit.SECONDS);
	}

	public void removeTemp() {
		this.redisTemplate.delete(getUqKey());
	}

	public long countErrorMsg() {
		return this.redisTemplate.opsForZSet().count(getUqKey(), 1.0D, 2.0D).longValue();
	}

	public List<T> listTemp() {
		return (List<T>) this.redisTemplate.opsForZSet().rangeByScore(getUqKey(), 0.0D, 1.0D).stream()
				.collect(Collectors.toList());
	}
}