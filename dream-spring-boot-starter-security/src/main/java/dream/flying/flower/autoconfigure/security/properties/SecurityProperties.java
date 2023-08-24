package dream.flying.flower.autoconfigure.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * SpringSecurity相关配置
 *
 * @author 飞花梦影
 * @date 2023-08-04 17:09:28
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@ConfigurationProperties("dream.security")
@Getter
@Setter
public class SecurityProperties {

	private boolean enabled = true;

	/**
	 * HttpSecurity 请求需要忽略的URL地址
	 */
	private String[] httpIgnoreResources;

	/**
	 * WebSecurity 需要忽略的资源
	 */
	private String[] webIgnoreResources;
}