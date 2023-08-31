package dream.flying.flower.autoconfigure.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wy.lang.StrHelper;

import dream.flying.flower.autoconfigure.security.cache.TokenStoreCache;
import dream.flying.flower.autoconfigure.security.entity.SecurityUserDetails;
import dream.flying.flower.autoconfigure.security.helper.TokenHelpers;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证过滤器
 *
 * @author 飞花梦影
 * @date 2023-07-08 17:06:23
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	private TokenStoreCache tokenStoreCache;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String accessToken = TokenHelpers.getAccessToken(request);
		log.info("@@@the request url:{}", request.getRequestURI());
		// accessToken为空,表示未登录,直接放过由SpringSecurity抛出异常
		if (StrHelper.isBlank(accessToken)) {
			chain.doFilter(request, response);
			return;
		}

		// 获取登录用户信息
		SecurityUserDetails user = tokenStoreCache.getUser(accessToken);
		// 获取失败,表示未登录,直接放过由SpringSecurity抛出异常
		if (user == null) {
			chain.doFilter(request, response);
			return;
		}

		// 用户存在
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

		// 新建 SecurityContext
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		chain.doFilter(request, response);
	}
}