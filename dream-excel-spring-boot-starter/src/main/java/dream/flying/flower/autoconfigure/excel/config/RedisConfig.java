package dream.flying.flower.autoconfigure.excel.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * Redis缓存配置
 *
 * @author 飞花梦影
 * @date 2023-08-10 14:35:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	@ConditionalOnMissingBean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<Object> jacksonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
		jacksonRedisSerializer.setObjectMapper(om);

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setValueSerializer(jacksonRedisSerializer);

		redisTemplate.setHashKeySerializer(RedisSerializer.string());
		redisTemplate.setHashValueSerializer(jacksonRedisSerializer);
		return redisTemplate;
	}

	@Bean
	@ConditionalOnMissingBean
	CacheManager cacheManager(RedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<Object> jacksonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		// 解决查询缓存转换异常的问题
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		jacksonRedisSerializer.setObjectMapper(om);
		// 配置序列化,解决乱码的问题,过期时间600秒
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(600))
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jacksonRedisSerializer))
				.disableCachingNullValues();
		return RedisCacheManager.builder(factory).cacheDefaults(config).build();
	}
}