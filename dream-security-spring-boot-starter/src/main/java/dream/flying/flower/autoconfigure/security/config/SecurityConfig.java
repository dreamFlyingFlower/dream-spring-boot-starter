package dream.flying.flower.autoconfigure.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dream.flying.flower.autoconfigure.security.exception.SecurityAuthenticationEntryPoint;
import dream.flying.flower.autoconfigure.security.filter.AuthenticationTokenFilter;
import dream.flying.flower.autoconfigure.security.handler.SecurityFailureHandler;
import dream.flying.flower.autoconfigure.security.handler.SecurityLogoutHandler;
import dream.flying.flower.autoconfigure.security.handler.SecuritySuccessHandler;
import dream.flying.flower.autoconfigure.security.mobile.MobileAuthenticationProvider;
import dream.flying.flower.autoconfigure.security.mobile.service.MobileUserDetailsService;
import dream.flying.flower.autoconfigure.security.mobile.service.MobileVerifyCodeService;
import dream.flying.flower.autoconfigure.security.properties.SecurityProperties;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * Security配置
 *
 * @author 飞花梦影
 * @date 2022-06-17 16:41:00
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

	private SecuritySuccessHandler securitySuccessHandler;

	private SecurityFailureHandler securityFailureHandler;

	private SecurityLogoutHandler securityLogoutHandler;

	private AuthenticationTokenFilter authenticationTokenFilter;

	private UserDetailsService userDetailsService;

	private MobileUserDetailsService mobileUserDetailsService;

	private MobileVerifyCodeService mobileVerifyCodeService;

	private PasswordEncoder passwordEncoder;

	private ApplicationEventPublisher applicationEventPublisher;

	private SecurityProperties securityProperties;

	// @Autowired
	// private AuthenticationDetailsSource<HttpServletRequest,
	// WebAuthenticationDetails> integrationWebAuthenticationDetailsSource;
	/**
	 * 普通用户数据库用户名密码登录
	 * 
	 * @return DaoAuthenticationProvider
	 */
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		return daoAuthenticationProvider;
	}

	/**
	 * 手机验证码登录
	 * 
	 * @return MobileAuthenticationProvider
	 */
	@Bean
	MobileAuthenticationProvider mobileAuthenticationProvider() {
		return new MobileAuthenticationProvider(mobileUserDetailsService, mobileVerifyCodeService);
	}

	// /**
	// * 集成各种网页登录模式
	// *
	// * @return IntegrationUserDetailsAuthenticationProvider
	// */
	// @Bean
	// IntegrationUserDetailsAuthenticationProvider
	// integrationUserDetailsAuthenticationProvider() {
	// return new
	// IntegrationUserDetailsAuthenticationProvider(integrationUserDetailsAuthenticationHandler());
	// }
	//
	// @Bean
	// public IntegrationUserDetailsAuthenticationHandler
	// integrationUserDetailsAuthenticationHandler() {
	// IntegrationUserDetailsAuthenticationHandler authenticationHandler =
	// new IntegrationUserDetailsAuthenticationHandler();
	// return authenticationHandler;
	// }

	/**
	 * 添加各种登录解析器,可自定义添加
	 * 
	 * @return AuthenticationManager
	 */
	@Bean
	public AuthenticationManager authenticationManager() {
		List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
		authenticationProviders.add(daoAuthenticationProvider());
		authenticationProviders.add(mobileAuthenticationProvider());
		// authenticationProviders.add(integrationUserDetailsAuthenticationProvider());
		ProviderManager providerManager = new ProviderManager(authenticationProviders);
		providerManager
				.setAuthenticationEventPublisher(new DefaultAuthenticationEventPublisher(applicationEventPublisher));
		return providerManager;
	}

	/**
	 * SpringSecurity HttpSecurity安全拦截
	 * 
	 * @param httpSecurity
	 * @return SecurityFilterChain
	 */
	@Bean
	@SneakyThrows
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
		httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
				// 禁止使用HttpSession
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// 不需要校验的请求
				.authorizeRequests().antMatchers(securityProperties.getHttpIgnoreResources()).permitAll()
				// 不需要校验的请求方式
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				// 其他请求都需要校验
				.anyRequest().authenticated().and()
				// 登录相关
				.formLogin().loginProcessingUrl("/login").successHandler(securitySuccessHandler)
				.failureHandler(securityFailureHandler).permitAll().and()
				// 登出相关
				.logout().logoutUrl("/logout").logoutSuccessHandler(securityLogoutHandler).deleteCookies("JSESSIONID")
				// 异常相关
				.and().exceptionHandling().authenticationEntryPoint(new SecurityAuthenticationEntryPoint()).and()
				.headers().frameOptions().disable().and().csrf().disable();

		return httpSecurity.build();
	}

	/**
	 * SpringSecurity WebSecurity安全拦截
	 * 
	 * @return WebSecurityCustomizer
	 */
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return webSecurity -> webSecurity
				// 去除验url非法验证防火墙
				// web.httpFirewall(new DefaultHttpFirewall());//StrictHttpFirewall
				.ignoring().antMatchers(securityProperties.getWebIgnoreResources());
	}
}