package dream.flying.flower.autoconfigure.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域请求
 * 
 * @author 飞花梦影
 * @date 2019-05-09 15:56:54
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
public class CorsConfig {

	@Bean
	@ConditionalOnMissingBean
	CorsFilter corsFilter() {
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.addAllowedMethod("*");

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

	@Bean
	@ConditionalOnMissingBean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// 允许跨域的站点
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedOriginPattern("*");
		// 允许跨域的请求头
		corsConfiguration.addAllowedHeader("*");
		// 允许跨域的请求类型
		corsConfiguration.addAllowedMethod("*");
		// 允许携带凭证
		corsConfiguration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}
}