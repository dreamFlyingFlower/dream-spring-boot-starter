package dream.flying.flower.autoconfigure.web.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 自定义Web配置
 *
 * @author 飞花梦影
 * @date 2022-12-08 16:14:59
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@ConfigurationProperties(prefix = "dream.web-mvc")
@ConditionalOnMissingBean
public class SelfWebMvcProperties {

	private Boolean enabled = true;

	/**
	 * 是否在输出到Web端时将Long类型转成字符串,以防止前端Long精度丢失(如雪花算法的ID)
	 */
	private Boolean enableLongToString = true;

	private WebMvcSerializeProperties serialize = new WebMvcSerializeProperties();

	/**
	 * 是否格式化LocalDateTime
	 */
	private Boolean enableLocalDateTimeFormat = true;
}