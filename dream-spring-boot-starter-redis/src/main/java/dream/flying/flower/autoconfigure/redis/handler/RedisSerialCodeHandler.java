package dream.flying.flower.autoconfigure.redis.handler;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import dream.flying.flower.autoconfigure.redis.helper.RedisStrHelpers;
import dream.flying.flower.framework.core.constant.ConstRedis;
import dream.flying.flower.framework.web.handler.SerialCodeHandler;
import dream.flying.flower.lang.StrHelper;

/**
 * 序列化编码
 *
 * @author 飞花梦影
 * @date 2024-03-29 22:30:22
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@ConditionalOnMissingBean
@Configuration
public class RedisSerialCodeHandler implements SerialCodeHandler {

	@Autowired
	private RedisStrHelpers redisStrHelpers;

	@Override
	public String obtainCode(String prefix, int length) {
		Long index = redisStrHelpers.incr(prefix, 1L);
		DecimalFormat numberFormat = new DecimalFormat(StrHelper.repeat("0", length));
		return prefix + numberFormat.format(index);
	}

	@Override
	public String obtainCodeWithDate(String prefix, int length) {
		String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String redisKey = ConstRedis.buildKey(prefix, localDate);
		Long index = redisStrHelpers.incr(redisKey, 1L);
		if (1 == index) {
			redisStrHelpers.setExpire(redisKey, index.toString(), 1, TimeUnit.DAYS);
		}
		DecimalFormat numberFormat = new DecimalFormat(StrHelper.repeat("0", length));
		return prefix + localDate + numberFormat.format(index);
	}

	@Override
	public List<String> obtainCodesWithDate(String prefix, int length, long delta) {
		delta = delta < 1 ? 1 : delta;
		String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String redisKey = ConstRedis.buildKey(prefix, localDate);
		String value = redisStrHelpers.get(redisKey);
		Long begin = StrHelper.isBlank(value) ? 0L : Long.parseLong(value);
		Long end = redisStrHelpers.incr(redisKey, delta);
		if (StrHelper.isBlank(value)) {
			redisStrHelpers.setExpire(redisKey, end.toString(), 1, TimeUnit.DAYS);
		}
		DecimalFormat numberFormat = new DecimalFormat(StrHelper.repeat("0", length));
		List<String> serialCodes = new ArrayList<>();
		for (long i = begin + 1; i <= end; i++) {
			serialCodes.add(prefix + localDate + numberFormat.format(i));
		}
		return serialCodes;
	}
}