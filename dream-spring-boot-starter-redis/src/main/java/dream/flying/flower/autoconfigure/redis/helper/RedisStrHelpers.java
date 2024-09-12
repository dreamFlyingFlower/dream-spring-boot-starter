package dream.flying.flower.autoconfigure.redis.helper;

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
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import com.alibaba.fastjson2.JSON;

import dream.flying.flower.digest.DigestHelper;
import dream.flying.flower.framework.core.constant.ConstRedis;
import dream.flying.flower.framework.core.enums.RedisKey;
import dream.flying.flower.framework.core.json.FastjsonHelpers;
import lombok.extern.slf4j.Slf4j;

/**
 * redis缓存中设置和获取值,key的类型全部都是string,若是需要特殊类型的key,使用不带后缀的set和get
 * 用什么opsFor类型存,就必须用该类型取,否则会报错
 * 所有set方法后带out的代表会设置超时时间,默认是30分钟;不带out的都是没有过期时间的,最好是设置超时,避免内存溢出
 * 
 * @auther 飞花梦影
 * @date 2018-07-23 19:50:45
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
@Scope("singleton")
@AutoConfigureAfter(StringRedisTemplate.class)
@Slf4j
public class RedisStrHelpers {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 原子比较redis集群中的值并删除redis当前key
	 * 
	 * @param key key
	 * @param value value
	 * @param failCallback 失败回调
	 * @param successCallback 成功回调
	 */
	public void atomicCompareAndDelete(String key, Object value, Consumer<StringRedisTemplate> failCallback,
			Consumer<StringRedisTemplate> successCallback) {
		Long result = stringRedisTemplate.execute(
				new DefaultRedisScript<Long>(ConstRedis.SCRIPT_COMPARE_AND_DELETE, Long.class), Arrays.asList(key),
				value);
		if (result == 0L) {
			// 失败
			if (Objects.nonNull(failCallback)) {
				failCallback.accept(stringRedisTemplate);
			}
		} else {
			if (Objects.nonNull(successCallback)) {
				successCallback.accept(stringRedisTemplate);
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
		return stringRedisTemplate.delete(key);
	}

	/**
	 * 批量删除缓存
	 * 
	 * @param keys 缓存key集合
	 * @return 清除成功的缓存数
	 */
	public Long clear(Collection<String> keys) {
		return stringRedisTemplate.delete(keys);
	}

	/**
	 * 清除redis中的所有缓存,若是redis清除某个key时,并没有这个key,返回0
	 * 无论是用delete或者设置key的超时时间都无法清除key是乱码的数据
	 */
	public Long clearAll() {
		return stringRedisTemplate.delete(keys());
	}

	/**
	 * 删除单个元素
	 * 
	 * @param redisKey redis中的key
	 * @return 删除成功的个数
	 */
	public boolean delete(String redisKey) {
		return stringRedisTemplate.delete(redisKey);
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
		return stringRedisTemplate.opsForList().remove(redisKey, count, value);
	}

	/**
	 * 删除map中的指定key
	 * 
	 * @param redisKey redis中的key
	 * @param hashKeys hash中的key
	 * @return 删除成功的个数
	 */
	public Long deleteMap(String redisKey, Object... hashKeys) {
		return stringRedisTemplate.opsForHash().delete(redisKey, hashKeys);
	}

	/**
	 * 判断redis中是否存在指定key
	 * 
	 * @param key key
	 * @return true->存在;false->不存在
	 */
	public boolean exist(String key) {
		return stringRedisTemplate.hasKey(key);
	}

	/**
	 * 获取key指向的值
	 * 
	 * @return 结果,可能为null
	 */
	public String get(Object key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 获取key指向的list集合中的所有元素
	 * 
	 * @param key key
	 * @return 结果,可能为null
	 */
	public List<String> getList(String key) {
		return stringRedisTemplate.opsForList().range(key, 0l, -1);
	}

	/**
	 * 获取key指向的list集合中下标为index的元素
	 * 
	 * @param key key
	 * @param index 索引
	 * @return 结果,可能为null
	 */
	public String getList(String key, Long index) {
		return stringRedisTemplate.opsForList().index(key, index);
	}

	/**
	 * 获取key指向的list集合中的从start开始到end结束索引的元素
	 * 
	 * @param key key
	 * @param start 开始索引
	 * @param end 结束索引
	 * @return 结果,可能为null
	 */
	public List<String> getList(String key, long start, long end) {
		return stringRedisTemplate.opsForList().range(key, start, end);
	}

	/**
	 * 获取key指向的list集合中元素的个数
	 * 
	 * @return 长度
	 */
	public Long getListSize(String key) {
		return stringRedisTemplate.opsForList().size(key);
	}

	/**
	 * 获取key指向的map中的所有值
	 * 
	 * @return 结果,可能为null
	 */
	public Map<Object, Object> getMap(String key) {
		return stringRedisTemplate.opsForHash().entries(key);
	}

	/**
	 * 获取key指向的map中的指定hashkey所关联的值
	 * 
	 * @return 结果,可能为null
	 */
	public Object getMap(String key, Object hashKey) {
		return stringRedisTemplate.opsForHash().get(key, hashKey);
	}

	/**
	 * 将数字类型的包装类从redis中取出,之后根据传入的数字类型强制转换
	 * 
	 * @param key key
	 * @param clazz 继承自Number的子类
	 * @return 数字结果
	 */
	public <T extends Number> T getNum(String key, Class<T> clazz) {
		return NumberUtils.parseNumber(stringRedisTemplate.opsForValue().get(key), clazz);
	}

	/**
	 * 获取key所指向的set中的所有值
	 * 
	 * @param key key
	 * @return Set<Object>
	 */
	public Set<String> getSet(String key) {
		return stringRedisTemplate.opsForSet().members(key);
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
		Object obj = stringRedisTemplate.opsForValue().get(key);
		return obj == null ? null : JSON.parseObject(obj.toString(), clazz);
	}

	/**
	 * 获取redis缓存中所有的key值
	 * 
	 * @return 所有的缓存key
	 */
	public Set<String> keys() {
		return stringRedisTemplate.keys("*");
	}

	/**
	 * 数据自增,原子操作
	 * 
	 * @param key 存储key
	 * @return 自增1之后的值
	 */
	public Long incr(String key) {
		return stringRedisTemplate.opsForValue().increment(key);
	}

	/**
	 * 数据自增,原子操作
	 * 
	 * @param key 存储key
	 * @param delta 自增的值
	 * @return 自增delta之后的值
	 */
	public Long incr(String key, Long delta) {
		return stringRedisTemplate.opsForValue().increment(key, delta);
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
		Boolean lock = stringRedisTemplate.opsForValue()
				.setIfAbsent(RedisKey.REDIS_KEY_LOCK.getKey(key), uuid, timeout <= 0 ? 100 : timeout,
						TimeUnit.MILLISECONDS);
		if (lock) {
			try {
				return function.apply(t);
			} finally {
				// 利用redis的脚本功能执行删除的操作,需要原子环境,防止锁刚过期,删除到其他人的锁.0删除失败,1删除成功
				stringRedisTemplate.execute(
						new DefaultRedisScript<Long>(ConstRedis.SCRIPT_COMPARE_AND_DELETE, Long.class),
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
	public Set<String> rangeByScore(String key, double scoure, double scoure1) {
		return stringRedisTemplate.opsForZSet().rangeByScore(key, scoure, scoure1);
	}

	/**
	 * 获取指定分值范围内的Set数据
	 *
	 * @param key key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return Set<ZSetOperations.TypedTuple<Object>>
	 */
	public Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, long start, long end) {
		return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
	}

	/**
	 * 有序集合获取排名
	 *
	 * @param key 集合名称
	 * @param value 值
	 * @return 排名
	 */
	public Long rank(String key, Object value) {
		return stringRedisTemplate.opsForZSet().rank(key, value);
	}

	/**
	 * 获取指定分值范围内的排名
	 *
	 * @param key key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return Set<ZSetOperations.TypedTuple<String>>
	 */
	public Set<ZSetOperations.TypedTuple<String>> reverseRangeByScore(String key, double min, double max) {
		return stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
	}

	/**
	 * 获取指定分值范围内的排名
	 *
	 * @param key key
	 * @param min 最小分值
	 * @param max 最大分值
	 * @return Set<ZSetOperations.TypedTuple<String>>
	 */
	public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScore(String key, long min, long max) {
		return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, min, max);
	}

	/**
	 * 有序集合添加
	 *
	 * @param key
	 * @param value
	 */
	public Double score(String key, Object value) {
		return stringRedisTemplate.opsForZSet().score(key, value);
	}

	/**
	 * 有序集合添加分数
	 *
	 * @param key
	 * @param value
	 * @param scoure
	 */
	public void scoreIncre(String key, String value, double scoure) {
		stringRedisTemplate.opsForZSet().incrementScore(key, value, scoure);
	}

	/**
	 * 将传入的key-value值缓存到redis中,不过期
	 * 
	 * @param key 存储key
	 * @param value 存储value
	 */
	public void set(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 给已有的key设置过期时间,默认30分钟
	 * 
	 * @param key key
	 */
	public void setExpire(String key) {
		stringRedisTemplate.expire(key, Duration.ofMinutes(ConstRedis.DEFAULT_EXPIRE_TIMEOUT));
	}

	/**
	 * 给已有的key设置过期时间
	 * 
	 * @param key key
	 * @param duration 过期时间
	 */
	public void setExpire(String key, Duration duration) {
		stringRedisTemplate.expire(key, duration);
	}

	/**
	 * 给已有的key设置过期时间
	 * 
	 * @param key key
	 * @param duration 过期时间
	 */
	public void setExpire(String key, long timeout, TimeUnit timeUnit) {
		stringRedisTemplate.expire(key, timeout, timeUnit);
	}

	/**
	 * 使用value方式存储缓存,默认过期时间30分钟
	 * 
	 * @param key key
	 * @param value value
	 */
	public void setExpire(String key, String value) {
		setExpire(key, value, ConstRedis.DEFAULT_EXPIRE_TIMEOUT);
	}

	/**
	 * 使用value方式存储缓存,并指定缓存过期时间
	 * 
	 * @param key key
	 * @param value value
	 * @param duration 超时时间
	 */
	public void setExpire(String key, String value, Duration duration) {
		stringRedisTemplate.opsForValue().set(key, value, duration);
	}

	/**
	 * 使用value方式存储缓存,并指定过期时间
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 超时时间,默认单位为分钟
	 */
	public void setExpire(String key, String value, long timeout) {
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
	public void setExpire(String key, String value, long timeout, TimeUnit timeUnit) {
		stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
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
		stringRedisTemplate.opsForValue().set(key, FastjsonHelpers.toJson(value));
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
	public <T> void setJsonExpire(String key, T value, Duration duration) {
		stringRedisTemplate.opsForValue().set(key, FastjsonHelpers.toJson(value), duration);
	}

	/**
	 * 使用value方式存储对象的json数据,并指定过期时间
	 * 
	 * @param <T>
	 * @param key key
	 * @param value value
	 * @param timeout 超时时间,默认单位分钟
	 */
	public <T> void setJsonExpire(String key, T value, long timeout) {
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
	public <T> void setJsonExpire(String key, T value, long timeout, TimeUnit timeUnit) {
		stringRedisTemplate.opsForValue().set(key, FastjsonHelpers.toJson(value), timeout, timeUnit);
	}

	/**
	 * 将数据存入到一个list集合中
	 * 
	 * @param key 存储key
	 * @param value 存储value
	 * @param index 存储到List的起始索引
	 */
	public void setList(String key, String value, Long index) {
		if (stringRedisTemplate.hasKey(key)) {
			stringRedisTemplate.opsForList().set(key, index, value);
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
	public void setListLeft(String key, List<String> values) {
		stringRedisTemplate.opsForList().leftPushAll(key, values);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相反,默认30分钟过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListLeftExpire(String key, List<String> vals) {
		setListLeftExpire(key, vals, 30);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相反,并设置过期时间
	 * 
	 * @param key key
	 * @param values values
	 * @param timeout 过期时间
	 */
	public void setListLeftExpire(String key, List<String> values, long timeout) {
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
	public void setListLeftExpire(String key, List<String> values, long timeout, TimeUnit timeUnit) {
		stringRedisTemplate.opsForList().rightPushAll(key, values);
		setExpire(stringRedisTemplate.opsForList().getOperations(), key, timeout, timeUnit);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,不过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListRight(String key, List<String> values) {
		stringRedisTemplate.opsForList().rightPushAll(key, values);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,不过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListRight(String key, String... values) {
		stringRedisTemplate.opsForList().rightPushAll(key, values);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,默认30分钟过期
	 * 
	 * @param key key
	 * @param values values
	 */
	public void setListRightExpire(String key, List<String> values) {
		setListRightExpire(key, values, 30);
	}

	/**
	 * 将整个list存入到缓存中,添加到缓存中元素的顺序和原list中序相同,并设置过期时间
	 * 
	 * @param key key
	 * @param values values
	 * @param timeout 过期时间
	 */
	public void setListRightExpire(String key, List<String> values, long timeout) {
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
	public void setListRightExpire(String key, List<String> values, long timeout, TimeUnit timeUnit) {
		stringRedisTemplate.opsForList().rightPushAll(key, values);
		setExpire(stringRedisTemplate.opsForList().getOperations(), key, timeout, timeUnit);
	}

	/**
	 * 将一整个map存入到redis缓存中,不过期
	 * 
	 * @param redisKey redis中的key值
	 * @param values 一个map对象
	 */
	public void setMap(String redisKey, Map<Object, Object> values) {
		stringRedisTemplate.opsForHash().putAll(redisKey, values);
	}

	/**
	 * 将数据存入到一个map中,一次存一个键值对,调用该方法前必须调用putall方法,否则redis中没有该key,会报错
	 * 
	 * @param redisKey redis中key值
	 * @param hashKey map中的key值
	 * @param hashValue map中的value值
	 */
	public void setMap(String redisKey, Object hashKey, Object hashValue) {
		stringRedisTemplate.opsForHash().put(redisKey, hashKey, hashValue);
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
		stringRedisTemplate.opsForHash().putAll(redisKey, values);
		setExpire(stringRedisTemplate.opsForHash().getOperations(), redisKey, duration);
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
		stringRedisTemplate.opsForHash().putAll(redisKey, values);
		setExpire(stringRedisTemplate.opsForHash().getOperations(), redisKey, timeout, timeUnit);
	}

	/**
	 * 将数字存入缓存
	 * 
	 * @param key key
	 * @param value 数字类值
	 */
	public void setNum(String key, Number value) {
		stringRedisTemplate.opsForValue().set(key, value.toString());
	}

	/**
	 * 当redis中没有值时才设置,有值时不设置,默认30S过期
	 * 
	 * @param key key
	 * @param value value
	 * @return true->redis中没有该key,设置成功;false->redis中已经存在该key,设置失败
	 */
	public boolean setNX(String key, String value) {
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
	public boolean setNX(String key, String value, Duration duration) {
		return stringRedisTemplate.opsForValue().setIfAbsent(key, value, duration);
	}

	/**
	 * 当redis中没有值时才设置,有值时不设置.默认过期时间单位为秒
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 过期时间
	 * @return true->redis中没有该key,设置成功;false->redis中已经存在该key,设置失败
	 */
	public boolean setNX(String key, String value, long timeout) {
		return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
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
	public boolean setNX(String key, String value, long timeout, TimeUnit timeUnit) {
		return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
	}

	/**
	 * 将数据追加到Set中,不过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSet(String key, Collection<String> vals) {
		return stringRedisTemplate.opsForSet().add(key, vals.toArray(new String[vals.size()]));
	}

	/**
	 * 将数据追加到Set中,不过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSet(String key, String... values) {
		return stringRedisTemplate.opsForSet().add(key, values);
	}

	/**
	 * 将数据追加到Set中,默认30分钟过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSetExpire(String key, Collection<String> values) {
		return setSetExpire(key, values.toArray(new String[values.size()]));
	}

	/**
	 * 将数据追加到Set中,默认30分钟过期
	 * 
	 * @param key key
	 * @param values 追加的值
	 * @return 追加成功的个数
	 */
	public Long setSetExpire(String key, String... values) {
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
	public Long setSetExpire(String key, long timeout, String... values) {
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
	public Long setSetExpire(String key, long timeout, TimeUnit timeUnit, String... values) {
		Long num = stringRedisTemplate.opsForSet().add(key, values);
		setExpire(stringRedisTemplate.opsForSet().getOperations(), key, timeout, timeUnit);
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
	public Boolean setZSet(String key, String value, double scoure) {
		return stringRedisTemplate.opsForZSet().add(key, value, scoure);
	}

	/**
	 * 当redis中有值时设置,没有值时不设置,默认30S过期
	 * 
	 * @param key key
	 * @param value value
	 * @return true->redis中有该key,设置成功;false->redis中不存在该key,设置失败
	 */
	public boolean setXX(String key, String value) {
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
	public boolean setXX(String key, String value, Duration duration) {
		return stringRedisTemplate.opsForValue().setIfPresent(key, value, duration);
	}

	/**
	 * 当redis中有值时设置,没有值时不设置.默认过期时间单位为秒
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout 过期时间
	 * @return true->redis中有该key,设置成功;false->redis中不存在该key,设置失败
	 */
	public boolean setXX(String key, String value, long timeout) {
		return stringRedisTemplate.opsForValue().setIfPresent(key, value, timeout, TimeUnit.SECONDS);
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
	public boolean setXX(String key, String value, long timeout, TimeUnit timeUnit) {
		return stringRedisTemplate.opsForValue().setIfPresent(key, value, timeout, timeUnit);
	}
}