package dream.flying.flower.autoconfigure.mybatis.plus.handler;

import com.baomidou.mybatisplus.annotation.TableField;

import dream.flying.flower.autoconfigure.mybatis.plus.properties.DreamMybatisPlusProperties;

/**
 * 当使用MyBatisPlus的注解{@link TableField#fill()}时会调用该方法
 *
 * @author 飞花梦影
 * @date 2023-01-11 15:33:13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class DefaultMybatisPlusHandler extends MybatisPlusHandler {

	public DefaultMybatisPlusHandler() {
		super(new DreamMybatisPlusProperties());
	}

	public DefaultMybatisPlusHandler(DreamMybatisPlusProperties mybatisPlusProperties) {
		super(mybatisPlusProperties);
	}
}