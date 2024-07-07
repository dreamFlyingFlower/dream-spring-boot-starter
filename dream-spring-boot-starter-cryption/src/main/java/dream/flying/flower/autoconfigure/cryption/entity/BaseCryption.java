package dream.flying.flower.autoconfigure.cryption.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 通用请求参数,主要用来做参数验证
 *
 * @author 飞花梦影
 * @date 2024-01-08 17:15:41
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseCryption {

	/**
	 * 请求时间戳
	 */
	private Long requestTime;
}