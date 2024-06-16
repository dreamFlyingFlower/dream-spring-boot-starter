package dream.flying.flower.autoconfigure.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.result.Result;

/**
 * 登录成功的回调
 *
 * @author 飞花梦影
 * @date 2022-06-17 16:52:21
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Configuration
public class SecuritySuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 允许跨域
		response.setHeader("Access-Control-Allow-Origin", "*");
		// 允许自定义请求头token(允许head跨域)
		response.setHeader("Access-Control-Allow-Headers",
				"token, Accept, Origin, X-Requested-With, Content-Type, Last-Modified");

		Result<?> result = Result.ok("登录成功", null);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(result));

		// 如果是要跳转到某个页面的,比如login的则
		// new DefaultRedirectStrategy().sendRedirect(request, response, "/login");
	}
}