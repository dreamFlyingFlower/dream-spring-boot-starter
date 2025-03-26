package dream.flying.flower.autoconfigure.cryption.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import dream.flying.flower.digest.enums.CryptType;
import lombok.Data;

/**
 * 加密配置
 *
 * @author 飞花梦影
 * @date 2022-12-20 14:50:34
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dream.encrypt-response")
public class EncryptResponseProperties {

	private boolean enabled = true;

	/**
	 * 加密密钥
	 */
	private String secretKey = "1234567890qazwsx";

	/**
	 * 加密类型
	 */
	private CryptType cryptType = CryptType.AES;

	/**
	 * 需要加密的类型,为null时除void之外都拦截
	 */
	private List<Class<?>> encryptClass;
}