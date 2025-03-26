package dream.flying.flower.autoconfigure.cryption.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import dream.flying.flower.digest.enums.CryptType;
import lombok.Data;

/**
 * 解密配置
 *
 * @author 飞花梦影
 * @date 2022-12-20 14:50:34
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dream.decrypt-request")
public class DecryptRequestProperties {

	private boolean enabled = true;

	/**
	 * 解密密钥
	 */
	private String secretKey = "1234567890qazwsx";

	/**
	 * 解密类型
	 */
	private CryptType cryptType = CryptType.AES;
}