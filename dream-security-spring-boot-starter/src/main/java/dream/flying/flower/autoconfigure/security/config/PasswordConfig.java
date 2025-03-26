package dream.flying.flower.autoconfigure.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 初始化需要生成的Bean
 *
 * @author 飞花梦影
 * @date 2022-11-14 10:28:02
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class PasswordConfig {

	@Bean
	@ConditionalOnMissingBean
	PasswordEncoder passwordEncoder() {
		// return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new BCryptPasswordEncoder();
	}
}