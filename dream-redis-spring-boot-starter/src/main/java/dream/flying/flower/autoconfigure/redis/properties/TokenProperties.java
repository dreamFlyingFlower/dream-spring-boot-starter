package dream.flying.flower.autoconfigure.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * token配置
 *
 * @author 飞花梦影
 * @date 2022-11-15 20:26:13
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@ConfigurationProperties(prefix = "dream.redis.token")
@Data
public class TokenProperties {

	String tokenIdempotent = "token_idempotent";

	String tokenLogin = "token_login";

	String tokenPrefix = "token_";
}