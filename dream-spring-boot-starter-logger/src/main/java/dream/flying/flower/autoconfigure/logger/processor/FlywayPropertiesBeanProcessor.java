package dream.flying.flower.autoconfigure.logger.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;

/**
 * 在{@link FlywayAutoConfiguration}调用{@link FlywayProperties}之前强制设置对象的属性
 *
 * @author 飞花梦影
 * @date 2025-03-23 11:13:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class FlywayPropertiesBeanProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (FlywayProperties.class == bean.getClass()) {
			FlywayProperties flywayProperties = (FlywayProperties) bean;
			// 防止生产环境误操作清除所有表,必须设置为true
			flywayProperties.setCleanDisabled(true);
			// 首次迁移时基线化非空数据库
			flywayProperties.setBaselineOnMigrate(true);
		}
		return bean;
	}
}