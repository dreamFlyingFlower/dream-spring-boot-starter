package dream.flying.flower.autoconfigure.web.helper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import com.alibaba.fastjson2.JSON;
import com.dream.digest.DigestHelper;

import dream.flying.flower.autoconfigure.web.config.RedisConfig;
import dream.framework.core.constant.ConstRedis;
import dream.framework.core.enums.RedisKey;
import dream.framework.core.json.FastjsonHelpers;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis工具类
 * 
 * @auther 飞花梦影
 * @date 2018-07-23 19:50:45
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@AutoConfigureAfter(RedisConfig.class)
@Scope("singleton")
@ConditionalOnBean(value = { RedisTemplate.class }, name = "redisTemplate")
@Slf4j
public class RedisHelpers {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 原子比较redis集群中的值并删除redis当前key
	 * 
	 * @param key key,脚本参数
	 * @param value value value,脚本值
	 */
	public void atomicCompareAndDelete(String key, Object value) {
		redisTemplate.execute(new DefaultRedisScript<Long>(ConstRedis.SCRIPT_COMPARE_AND_DELETE, Long.class),
				Arrays.asList(key), value);
	}

	/**
	 * 原子比较redis集群中的值并删除redis当前key
	 * 
	 * @param key key
	 * @param value value
	 * @param failCallback 失败回调
	 * @param successCallback 成功回调
	 */
	public void atomicCompareAndDelete(String key, Object value, Consumer<RedisTemplate<String, Object>> failCallback,
			Consumer<RedisTemplate<String, Object>> successCallback) {
		Long result =
				redisTemplate.execute(new DefaultRedisScript<Long>(ConstRedis.SCRIPT_COMPARE_AND_DELETE, Long.class),
						Arrays.asList(key), value);
		if (result == 0L) {
			// 失败
			if (Objects.nonNull(failCallback)) {
				failCallback.accept(redisTemplate);
			}
		} else {
			if (Objects.nonNull(successCallback)) {
				successCallback.accept(redisTemplate);
			}
		}
	}

	/**
	 * 删除单个缓存
	 * 
	 * @param key 缓存key
	 * @return true->成功,false->失败
	 */
	public Boolean clear(String key) {
		return redisTemplate.delete(key);
	}

	/**
	 * 批量删除缓存
	 * 
	 * @param keys 缓存key集合
	 * @return 清除成功的缓存数
	 */
	public Long clear(Collection<String> keys) {
		return redisTemplate.delete(keys);
	}

	/**
	 * 清除redis中的所有缓存,若是redis清除某个key时,并没有这个key,返回0
	 * 无论是用delete或者设置key的超时时间都无法清除key是乱码的数据
	 */
	public Long clearAll() {
		return redisTemplate.delete(keys());
	}

	/**
	 * 从list中删除count个元素,从value第一次出现开始
	 * 
	 * @param redisKey redis中的key
	 * @param count 删除个数
	 * @param value 删除的元素
	 * @return 删除成功的个数
	 */
	public Long deleteList(String redisKey, Object value, Long count) {
		return redisTemplate.opsForList().remove(redisKey, count, value);
	}

	/**
	 * 删除map中的指定key
	 * 
	 * @param redisKey redis中的key
	 * @param hashKeys hash中的key
	 * @return 删除成功的个数
	 */
	public Long deleteMap(String redisKey, Object... hashKeys) {
		return redisTemplate.opsForHash().delete(redisKey, hashKeys);
	}

	/**
	 * 判断redis中是否存在指定key
	 * 
	 * @param key key
	 * @return true->存在;false->不存在
	 */
	public boolean exist(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 获取key指向的值
	 * 
	 * @return 结果,可能为null
	 */
	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * 获取key指向的list集合中的所有元素
	 * 
	 * @return 结果,可能为null
	 */
	public List<Object> getList(String key) {
		return redisTemplate.opsForList().range(key, 0l, -1);
	}

	/**
	 * 获取key指向的list集合中下标为index的元素
	 * 
	 * @return 结果,可能为null
	 */
	public Object getList(String key, Long index) {
		return redisTemplate.opsForList().index(key, index);
	}

	/**
	 * 获取key指向的list集合中元素的个数
	 * 
	 * @return 长度
	 */
	public Long getListSize(String key) {
		return redisTemplate.opsForList().size(key);
	}

	/**
	 * 获取key指向的map中的所有值
	 * 
	 * @return 结果,可能为null
	 */
	public Map<Object, Object> getMap(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	/**
	 * 获取key指向的map中的指定hashkey所关联的值
	 * 
	 * @return 结果,可能为null
	 */
	public Object getMap(String key, Object hashKey) {
		return redisTemplate.opsForHash().get(key, hashKey);
	}

	/**
	 * 将数字类型的包装类从redis中取出,之后根据传入的数字类型强制转换
	 * 
	 * @param key key
	 * @param clazz 继承自Number的子类
	 * @return 数字结果
	 */
	public <T extends Number> T getNum(String key, Class<T> clazz) {
		return NumberUtils.parseNumber(String.valueOf(redisTemplate.opsForValue().get(key)), clazz);
	}

	/**
	 * 获取key所指向的set中的所有值
	 * 
	 * @param key key
	 * @return Set<Object>
	 */
	public Set<Object> getSet(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	/**
	 * 获取指定对象数据,并转换为json输出
	 * 
	 * @param <T> 输出类字节码泛型
	 * @param key key
	 * @param clazz 类字节码
	 * @return T对象实例
	 */
	public <T> T getSynthetic(Object key, Class<T> clazz) {
		Object obj = redisTemplate.opsForValue().get(key);
		return obj == null ? null : JSON.parseObject(obj.toString(), clazz);
	}

	/**
	 * 获取redis缓存中所有的key值
	 * 
	 * @return 所有的缓存key
	 */
	public Set<String> keys() {
		return redisTemplate.keys("*");
	}

	/**
	 * 数据自增,原子操作
	 * 
	 * @param key 存储key
	 * @return 自增1之后的值
	 */
	public Long incr(String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	/**
	 * 数据自增,原子操作
	 * 
	 * @param key 存储key
	 * @param delta 自增的值
	 * @return 自增delta之后的值
	 */
	public Long incr(String key, Long delta) {
		return redisTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * 试用分布式锁,执行方法
	 * 
	 * @param key redis加锁的key
	 * @param function 需要执行的方法
	 * @param t 执行方法的参数
	 * @return 执行成功的返回值
	 */
	public <T, R> R lock(String key, Function<T, R> function, T t) {
		return lock(key, 100l, function, t);
	}

	/**
	 * 使用分布式锁执行方法
	 * 
	 * @param key redis加锁的key
	 * @param timeout 锁过期时间,默认100MS
	 * @param function 需要执行的方法
	 * @param t 执行方法的参数
	 * @return 执行成功的返回值
	 */
	public <T, R> R lock(String key, long timeout, Function<T, R> function, T t) {
		String uuid = DigestHelper.uuid();
		// 分布式锁占坑,设置过期时间,必须和加锁一起作为原子性操作
		Boolean lock = redisTemplate.opsForValue().setIfAbsent(RedisKey.REDIS_KEY_LOCK.getKey(key), uuid,
				timeout <= 0 ? 100 : timeout, TimeUnit.MILLISECONDS);
		if (lock) {
			try {
				return function.apply(t);
			} finally {
				// 利用redis的脚本功能执行删除的操作,需要原子环境,防止锁刚过期,删除到其他人的锁.0删除失败,1删除成功
				redisTemplate.execute(new DefaultRedisScript<Long>(ConstRedis.SCRIPT_COMPARE_AND_DELETE, Long.class),
						Arrays.asList(RedisKey.REDIS_KEY_LOCK.getKey(key)), uuid);
			}
		} else {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return lock(key, function, t);
		}
	}

	/**
	 * 获取指定分值范围内的Set数据
	 *
	 * @param key key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return Set<String>
	 */
	public Set<Object> rangeByScore(String key, double min, double max) {
		return redisTemplate.opsForZSet().rangeByScore(key, min, max);
	}

	/**
	 * 获取指定分值范围内的Set数据
	 *
	 * @param key key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return Set<ZSetOperations.TypedTuple<Object>>
	 */
	public Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(String key, long min, long max) {
		return redisTemplate.opsForZSet().rangeWithScores(key, min, max);
	}

	/**
	 * 有序集合获取排名
	 *
	 * @param key 集合名称
	 * @param value 值
	 * @return 排名
	 */
	public Long rank(String key, Object value) {
		return redisTemplate.opsForZSet().rank(key, value);
	}

	/**
	 * 获取指定分值范围内的排名
	 *
	 * @param key key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return Set<ZSetOperations.TypedTuple<String>>
	 */
	public Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScore(String key, double min, double max) {
		return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
	}

	/**
	 * 获取指定分值范围内的排名
	 *
	 * @param key key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return Set<ZSetOperations.TypedTuple<String>>
	 */
	public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScore(String key, long min, long max) {
		return redisTemplate.opsForZSet().reverseRangeWithScores(key, min, max);
	}

	/**
	 * 有序集合添加
	 *
	 * @param key
	 * @param value
	 */
	public Double score(String key, Object value) {
		return redisTemplate.opsForZSet().score(key, value);
	}

	/**
	 * 有序集合添加分数
	 *
	 * @param key
	 * @param value
	 * @param scoure
	 */
	public void scoreIncre(String key, Object value, double scoure) {
		redisTemplate.opsForZSet().incrementScore(key, value, scoure);
	}

	/**
	 * 将传入的key-value值缓存到redis中,不过期
	 * 
	 * @param key 存储key
	 * @param value 存储value
	 */
	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 使用value方式存储缓存,默认过期时间30分钟
	 * 
	 * @param key key
	 * @param value value
	 */
	public void setExpire(String key, Object value) {
		setExpire(key, value, 30L);
	}

	/**
	 * 使用value方式存储缓存,并指定缓存过期时间
	 * 
	 * @param key key
	 * @param value value
	 * @param duration 超时时间
	 */
	public void setExpire(String key, Object value, Duration duration) {
		redisTemplate.opsForValue().set(key, value, duration);
	}

	/**
	 * 使用value方式存储缓存,并指定过期时间
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 超时时间,默认单位为分钟
	 */
	public void setExpire(String key, Object value, long timeout) {
		setExpire(key, value, timeout, TimeUnit.MINUTES);
	}

	/**
	 * 使用value方式存储缓存,并指定过期时间
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 超时时间
	 * @param timeUnit 超时时间单位
	 */
	public void setExpire(String key, Object value, long timeout, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
	}

	/**
	 * 对已经存在的key设置过期时间,默认30分钟
	 * 
	 * @param redisOperations redis处理器
	 * @param key key
	 * @return true->成功,false->失败
	 */
	public boolean setExpire(RedisOperations<String, ?> redisOperations, String key) {
		return setExpire(redisOperations, key, 30);
	}

	/**
	 * 对已经存在的key设置过期时间
	 * 
	 * @param redisOperations redis处理器
	 * @param key key
	 * @param duration 过期时间
	 * @return true->成功,false->失败
	 */
	public boolean setExpire(RedisOperations<String, ?> redisOperations, String key, Duration duration) {
		return redisOperations.expire(key, duration);
	}

	/**
	 * 对已经存在的key设置过期时间
	 * 
	 * @param redisOperations redis处理器
	 * @param key key
	 * @param timeout 过期时间
	 * @return true->成功,false->失败
	 */
	public boolean setExpire(RedisOperations<String, ?> redisOperations, String key, long timeout) {
		return setExpire(redisOperations, key, timeout, TimeUnit.MINUTES);
	}

	/**
	 * 对已经存在的key设置过期时间
	 * 
	 * @param redisOperations redis处理器
	 * @param key key
	 * @param timeout 过期时间
	 * @param unit 时间单位
	 * @return true->成功,false->失败
	 */
	public boolean setExpire(RedisOperations<String, ?> redisOperations, String key, long timeout, TimeUnit unit) {
		return redisOperations.expire(key, timeout, unit);
	}

	/**
	 * 使用value方式存储对象的json数据,不过期
	 * 
	 * @param <T>
	 * @param key key
	 * @param value value
	 */
	public <T> void setJson(String key, T value) {
		redisTemplate.opsForValue().set(key, FastjsonHelpers.toJson(value));
	}

	/**
	 * 使用value方式存储对象的json数据,默认过期时间30分钟
	 * 
	 * @param <T>
	 * @param key key
	 * @param value value
	 */
	public <T> void setJsonExpire(String key, T value) {
		setJsonExpire(key, value, 30);
	}

	/**
	 * 使用value方式存储对象的json数据,并指定过期时间
	 * 
	 * @param <T>
	 * @param key key
	 * @param value value
	 * @param duration 超时时间
	 */
	public <T> void setJsonExpire(String key, Object value, Duration duration) {
		redisTemplate.opsForValue().set(key, FastjsonHelpers.toJson(value), duration);
	}

	/**
	 * 使用value方式存储对象的json数据,并指定过期时间
	 * 
	 * @param <T>
	 * @param key key
	 * @param value value
	 * @param timeout 超时时间,默认单位分钟
	 */
	public <T> void setJsonExpire(String key, Object value, long timeout) {
		setJsonExpire(key, value, timeout, TimeUnit.MINUTES);
	}

	/**
	 * 使用value方式存储对象的json数据,并指定过期时间
	 * 
	 * @param <T>
	 * @param key key
	 * @param value value
	 * @param timeout 超时时间
	 * @param timeUnit 超时时间单位
	 */
	public <T> void setJsonExpire(String key, T value, long timeout, TimeUnit unit) {
		redisTemplate.opsForValue().set(key, FastjsonHelpers.toJson(value), timeout, unit);
	}

	/**
	 * 将数据存入到一个list集合中,该集合中泛型不确定,可存入任何类型
	 * 
	 * @param key 存储key
	 * @param value 存储value
	 * @param index 存储到List的起始索引
	 */
	public void setList(String key, Object value, Long index) {
		if (redisTemplate.hasKey(key)) {
			redisTemplate.opsForList().set(key, index, value);
		} else {
			log.error("###:redis缓存中没有key值为{}的list集合", key);
		}
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相反,不过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListLeft(String key, List<Object> values) {
		redisTemplate.opsForList().leftPushAll(key, values);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相反,默认30分钟过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListLeftExpire(String key, List<Object> values) {
		setListLeftExpire(key, values, 30);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相反,并设置过期时间
	 * 
	 * @param key key
	 * @param values values
	 * @param timeout 过期时间
	 */
	public void setListLeftExpire(String key, List<Object> values, long timeout) {
		setListLeftExpire(key, values, timeout, TimeUnit.MINUTES);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相反,并设置过期时间
	 * 
	 * @param key key
	 * @param values values
	 * @param timeout 过期时间
	 * @param timeUnit 过期时间单位
	 */
	public void setListLeftExpire(String key, List<Object> values, long timeout, TimeUnit timeUnit) {
		redisTemplate.opsForList().rightPushAll(key, values);
		setExpire(redisTemplate.opsForList().getOperations(), key, timeout, timeUnit);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,不过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListRight(String key, List<Object> values) {
		redisTemplate.opsForList().rightPushAll(key, values);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,默认30分钟过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListRightExpire(String key, List<Object> values) {
		setListRightExpire(key, values, 30);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,并设置过期时间
	 * 
	 * @param key key
	 * @param values values
	 * @param timeout 过期时间
	 */
	public void setListRightExpire(String key, List<Object> values, long timeout) {
		setListRightExpire(key, values, timeout, TimeUnit.MINUTES);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,并设置过期时间
	 * 
	 * @param key key
	 * @param values values
	 * @param timeout 过期时间
	 * @param timeUnit 过期时间单位
	 */
	public void setListRightExpire(String key, List<Object> values, long timeout, TimeUnit timeUnit) {
		redisTemplate.opsForList().rightPushAll(key, values);
		setExpire(redisTemplate.opsForList().getOperations(), key, timeout, timeUnit);
	}

	/**
	 * 将一整个map存入到redis缓存中,不过期
	 * 
	 * @param key redis中key值
	 * @param values 一个map对象
	 */
	public void setMap(String key, Map<Object, Object> values) {
		redisTemplate.opsForHash().putAll(key, values);
	}

	/**
	 * 将数据存入到一个map中,一次存一个键值对,调用该方法前必须调用putall方法,否则redis中没有该key,会报错
	 * 
	 * @param key redis中key值
	 * @param hashKey map中的key值
	 * @param hashValue map中的value值
	 */
	public void setMap(String key, Object hashKey, Object hashValue) {
		redisTemplate.opsForHash().put(key, hashKey, hashValue);
	}

	/**
	 * 将一整个map存入到redis缓存中,默认30分钟过期
	 * 
	 * @param redisKey redis中的key值
	 * @param values 一个map对象
	 */
	public void setMapExpire(String redisKey, Map<Object, Object> values) {
		setMapExpire(redisKey, values, 30);
	}

	/**
	 * 将一整个map存入到redis缓存中,并设置过期时间
	 * 
	 * @param redisKey redis中的key值
	 * @param values 一个map对象
	 * @param duration 过期时间
	 */
	public void setMapExpire(String redisKey, Map<Object, Object> values, Duration duration) {
		redisTemplate.opsForHash().putAll(redisKey, values);
		setExpire(redisTemplate.opsForHash().getOperations(), redisKey, duration);
	}

	/**
	 * 将一整个map存入到redis缓存中,并设置过期时间
	 * 
	 * @param redisKey redis中的key值
	 * @param values 一个map对象
	 * @param timeout 过期时间,单位分钟
	 */
	public void setMapExpire(String redisKey, Map<Object, Object> values, long timeout) {
		setMapExpire(redisKey, values, timeout, TimeUnit.MINUTES);
	}

	/**
	 * 将一整个map存入到redis缓存中,过期
	 * 
	 * @param redisKey redis中的key值
	 * @param values 一个map对象
	 * @param timeout 过期时间,单位分钟
	 * @param timeUnit 过期时间单位
	 */
	public void setMapExpire(String redisKey, Map<Object, Object> values, long timeout, TimeUnit timeUnit) {
		redisTemplate.opsForHash().putAll(redisKey, values);
		setExpire(redisTemplate.opsForHash().getOperations(), redisKey, timeout, timeUnit);
	}

	/**
	 * 当redis中没有值时才设置,有值时不设置,默认30S过期
	 * 
	 * @param key key
	 * @param value value
	 * @return true->redis中没有该key,设置成功;false->redis中已经存在该key,设置失败
	 */
	public boolean setNX(String key, Object value) {
		return setNX(key, value, 30l);
	}

	/**
	 * 当redis中没有值时才设置,有值时不设置
	 * 
	 * @param key key
	 * @param value value
	 * @param duration 过期时间
	 * @return true->redis中没有该key,设置成功;false->redis中已经存在该key,设置失败
	 */
	public boolean setNX(String key, Object value, Duration duration) {
		return redisTemplate.opsForValue().setIfAbsent(key, value, duration);
	}

	/**
	 * 当redis中没有值时才设置,有值时不设置.默认过期时间单位为秒
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 过期时间
	 * @return true->redis中没有该key,设置成功;false->redis中已经存在该key,设置失败
	 */
	public boolean setNX(String key, Object value, long timeout) {
		return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 当redis中没有值时才设置,有值时不设置
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 过期时间
	 * @param timeUnit 过期时间单位
	 * @return true->redis中没有该key,设置成功;false->redis中已经存在该key,设置失败
	 */
	public boolean setNX(String key, Object value, long timeout, TimeUnit timeUnit) {
		return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
	}

	/**
	 * 将数据追加到Set中,不过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSet(String key, Collection<Object> values) {
		return setSet(key, values.toArray());
	}

	/**
	 * 将数据追加到Set中,不过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSet(String key, Object... values) {
		return redisTemplate.opsForSet().add(key, values);
	}

	/**
	 * 将数据追加到Set中,默认30分钟过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSetExpire(String key, Collection<Object> values) {
		return setSetExpire(key, values.toArray());
	}

	/**
	 * 将数据追加到Set中,默认30分钟过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSetExpire(String key, Object... values) {
		return setSetExpire(key, 30, values);
	}

	/**
	 * 将数据追加到Set中,并设置过期时间,默认时间单位分钟
	 * 
	 * @param key key
	 * @param timeout 过期时间
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSetExpire(String key, long timeout, Object... values) {
		return setSetExpire(key, timeout, TimeUnit.MINUTES, values);
	}

	/**
	 * 将数据追加到Set中,并设置过期时间
	 * 
	 * @param key key
	 * @param timeout 过期时间
	 * @param timeUnit 过期时间单位
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSetExpire(String key, long timeout, TimeUnit timeUnit, Object... values) {
		Long num = redisTemplate.opsForSet().add(key, values);
		setExpire(redisTemplate.opsForSet().getOperations(), key, timeout, timeUnit);
		return num;
	}

	/**
	 * ZSet添加
	 *
	 * @param key 缓存key
	 * @param value 缓存值
	 * @param scoure 分值
	 * @return true->成功;false->失败
	 */
	public Boolean setZSet(String key, Object value, double scoure) {
		return redisTemplate.opsForZSet().add(key, value, scoure);
	}

	/**
	 * 当redis中有值时设置,没有值时不设置,默认30S过期
	 * 
	 * @param key key
	 * @param value value
	 * @return true->redis中有该key,设置成功;false->redis中不存在该key,设置失败
	 */
	public boolean setXX(String key, Object value) {
		return setXX(key, value, 30l);
	}

	/**
	 * 当redis中有值时设置,没有值时不设置
	 * 
	 * @param key key
	 * @param value value
	 * @param duration 过期时间
	 * @return true->redis中有该key,设置成功;false->redis中不存在该key,设置失败
	 */
	public boolean setXX(String key, Object value, Duration duration) {
		return redisTemplate.opsForValue().setIfPresent(key, value, duration);
	}

	/**
	 * 当redis中有值时设置,没有值时不设置.默认过期时间单位为秒
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 过期时间
	 * @return true->redis中有该key,设置成功;false->redis中不存在该key,设置失败
	 */
	public boolean setXX(String key, Object value, long timeout) {
		return redisTemplate.opsForValue().setIfPresent(key, value, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 当redis中有值时设置,没有值时不设置
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 过期时间
	 * @param timeUnit 过期时间单位
	 * @return true->redis中有该key,设置成功;false->redis中不存在该key,设置失败
	 */
	public boolean setXX(String key, Object value, long timeout, TimeUnit timeUnit) {
		return redisTemplate.opsForValue().setIfPresent(key, value, timeout, timeUnit);
	}
}