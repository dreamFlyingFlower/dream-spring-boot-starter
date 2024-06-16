package dream.flying.flower.autoconfigure.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import dream.flying.flower.framework.web.request.ReusableHttpServletRequestWrapper;

/**
 * 拦截输入流,让输入流可重复使用
 *
 * @author 飞花梦影
 * @date 2022-12-21 10:52:12
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ServletRequestWrapperFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		ReusableHttpServletRequestWrapper reusableHttpServletRequestWrapper =
				new ReusableHttpServletRequestWrapper(httpServletRequest);

		chain.doFilter(reusableHttpServletRequestWrapper, response);
	}
}