package dream.flying.flower.autoconfigure.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
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
		// 是否允许请求带有验证信息
		corsConfiguration.setAllowCredentials(true);
		// 当allowCredentials为true时,allowedOrigin必须指明允许的头,不能使用*,但是可以使用allowedOriginPattern("*")
		// corsConfiguration.addAllowedOrigin("*");
		// 允许向该服务器提交请求的URI,*表示全部允许,在SpringMVC中,如果设成*,会自动转成当前请求头中的Origin
		corsConfiguration.addAllowedOriginPattern("*");
		// corsConfiguration.addAllowedOrigin("*");
		// 允许访问的头信息,*表示全部
		corsConfiguration.addAllowedHeader("*");
		// 允许自定义的头部,大小写不敏感
		// corsConfiguration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers",
		// "Accept", "Origin",
		// "Content-Disposition", "Authorization", "No-Cache", "X-Requested-With",
		// "If-Modified-Since", "Pragma",
		// "Last-Modified", "Cache-Control", "Expires", "Content-Type", "X-E4M-With",
		// "userId", "token"));
		// 预检请求的缓存时间(秒),即在这个时间段里,对于相同的跨域请求不会再预检了
		corsConfiguration.setMaxAge(3600L);
		// 允许提交请求的方法类型,*表示全部允许
		corsConfiguration.addAllowedMethod("*");
		// corsConfiguration.addAllowedMethod("OPTIONS");
		// corsConfiguration.addAllowedMethod("HEAD");
		// corsConfiguration.addAllowedMethod("GET");
		// corsConfiguration.addAllowedMethod("PUT");
		// corsConfiguration.addAllowedMethod("POST");
		// corsConfiguration.addAllowedMethod("DELETE");
		// corsConfiguration.addAllowedMethod("PATCH");

		// 扩展请求头
		// corsConfiguration.addExposedHeader("X-Auth-Token");

		// 允许脚本访问的返回头
		// corsConfiguration.setHeader("Access-Control-Expose-Headers", arg1);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}
}