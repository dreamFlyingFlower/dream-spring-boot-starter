package dream.flying.flower.autoconfigure.excel.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-17 19:45:45
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public abstract class AbstractQueryData<T> implements QueryData<T>, ParamHandler {

	@Resource
	private RedisTemplate<String, T> redisTemplate;

	public final int expireSecond = 600;

	public Map<String, Object> getPageTemp(int start, int end) {
		List<T> list = (List<T>) this.redisTemplate.opsForZSet().range(getUqKey(), start, end).stream()
				.collect(Collectors.toList());
		long count = this.redisTemplate.opsForZSet().count(getUqKey(), 0.0D, 0.0D).longValue();
		Map<String, Object> map = new HashMap<>(2);
		map.put("list", list);
		map.put("count", Long.valueOf(count));
		return map;
	}

	public List<T> listTemp() {
		return (List<T>) this.redisTemplate.opsForZSet().rangeByScore(getUqKey(), 0.0D, 1.0D).stream()
				.collect(Collectors.toList());
	}
}