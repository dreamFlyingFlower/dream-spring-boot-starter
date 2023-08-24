package dream.flying.flower.autoconfigure.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 加密参数
 *
 * @author 飞花梦影
 * @date 2022-12-20 14:50:34
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@ConfigurationProperties(prefix = "dream.crypto")
public class CryptoProperties {

	private String paramSecret = "1234567890qazwsx";
}