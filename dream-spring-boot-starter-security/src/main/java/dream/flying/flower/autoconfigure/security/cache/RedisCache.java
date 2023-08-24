package dream.flying.flower.autoconfigure.security.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis缓存
 *
 * @author 飞花梦影
 * @date 2023-07-08 17:03:39
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
public class RedisCache {

	/**
	 * 默认过期时长为24小时，单位：秒
	 */
	public final static long DEFAULT_EXPIRE = 60 * 60 * 24L;

	/**
	 * 过期时长为1小时，单位：秒
	 */
	public final static long HOUR_ONE_EXPIRE = 60 * 60 * 1L;

	/**
	 * 过期时长为6小时，单位：秒
	 */
	public final static long HOUR_SIX_EXPIRE = 60 * 60 * 6L;

	/**
	 * 不设置过期时长
	 */
	public final static long NOT_EXPIRE = -1L;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public void set(String key, Object value, long expire) {
		redisTemplate.opsForValue().set(key, value);
		if (expire != NOT_EXPIRE) {
			expire(key, expire);
		}
	}

	public void set(String key, Object value) {
		set(key, value, DEFAULT_EXPIRE);
	}

	public Object get(String key, long expire) {
		Object value = redisTemplate.opsForValue().get(key);
		if (expire != NOT_EXPIRE) {
			expire(key, expire);
		}
		return value;
	}

	public Object get(String key) {
		return get(key, NOT_EXPIRE);
	}

	public Long increment(String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	public Boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public void delete(Collection<String> keys) {
		redisTemplate.delete(keys);
	}

	public Object hGet(String key, String field) {
		return redisTemplate.opsForHash().get(key, field);
	}

	public Map<String, Object> hGetAll(String key) {
		HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
		return hashOperations.entries(key);
	}

	public void hMSet(String key, Map<String, Object> map) {
		hMSet(key, map, DEFAULT_EXPIRE);
	}

	public void hMSet(String key, Map<String, Object> map, long expire) {
		redisTemplate.opsForHash().putAll(key, map);

		if (expire != NOT_EXPIRE) {
			expire(key, expire);
		}
	}

	public void hSet(String key, String field, Object value) {
		hSet(key, field, value, DEFAULT_EXPIRE);
	}

	public void hSet(String key, String field, Object value, long expire) {
		redisTemplate.opsForHash().put(key, field, value);

		if (expire != NOT_EXPIRE) {
			expire(key, expire);
		}
	}

	public void expire(String key, long expire) {
		redisTemplate.expire(key, expire, TimeUnit.SECONDS);
	}

	public void hDel(String key, Object... fields) {
		redisTemplate.opsForHash().delete(key, fields);
	}

	public void leftPush(String key, Object value) {
		leftPush(key, value, DEFAULT_EXPIRE);
	}

	public void leftPush(String key, Object value, long expire) {
		redisTemplate.opsForList().leftPush(key, value);

		if (expire != NOT_EXPIRE) {
			expire(key, expire);
		}
	}

	public Object rightPop(String key) {
		return redisTemplate.opsForList().rightPop(key);
	}

	public Set<String> getKeys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	public Set<String> getKeys() {
		return getKeys("*");
	}

	public Boolean exists(String key) {
		return redisTemplate.hasKey(key);
	}

	public void del(String key) {
		redisTemplate.delete(key);
	}

	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public void set(String key, String value, Integer expire) {
		redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
	}

	public void expire(String key, int expire) {
		redisTemplate.expire(key, expire, TimeUnit.SECONDS);
	}

	public void append(String key, String value) {
		redisTemplate.opsForValue().append(key, value);
	}

	public Object getset(String key, Object newValue) {
		return redisTemplate.opsForValue().getAndSet(key, newValue);
	}

	public boolean setnx(String key, String value) {
		return redisTemplate.opsForValue().setIfAbsent(key, value);
	}

	public Long incrBy(String key, Long delta) {
		return redisTemplate.opsForValue().increment(key, delta);
	}
}