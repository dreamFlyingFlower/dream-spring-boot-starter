package dream.flying.flower.autoconfigure.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 初始化需要生成的Bean
 *
 * @author 飞花梦影
 * @date 2022-11-14 10:28:02
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class AwareConfig {

	// @Bean
	// @ConditionalOnMissingBean
	// OperateLogService asyncLogService() {
	// return new OperateLogServiceImpl();
	// }

	@Bean
	@ConditionalOnMissingBean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}