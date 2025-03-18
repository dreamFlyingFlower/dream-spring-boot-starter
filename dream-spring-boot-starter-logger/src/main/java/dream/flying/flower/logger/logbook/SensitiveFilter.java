package dream.flying.flower.logger.logbook;

import java.util.regex.Pattern;

import org.zalando.logbook.BodyFilter;

/**
 * 敏感信息过滤
 *
 * @author 飞花梦影
 * @date 2025-03-18 21:27:10
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class SensitiveFilter implements BodyFilter {

	private static final Pattern PATTERN = Pattern.compile("(\"password\":\")([^\"]+)");

	@Override
	public String filter(String contentType, String body) {
		return PATTERN.matcher(body).replaceAll("$1******");
	}
}