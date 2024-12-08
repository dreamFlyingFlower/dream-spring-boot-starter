package dream.flying.flower.autoconfigure.captcha;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码参数类
 *
 * @author 飞花梦影
 * @date 2024-12-08 22:26:55
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageCaptcha {

	/**
	 * 验证码唯一校验
	 */
	private String state;

	/**
	 * Base64验证码图形
	 */
	private String image;
}