package dream.flying.flower.autoconfigure.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.wy.result.Result;

import dream.framework.web.helper.WebHelper;

/**
 * 登出成功
 *
 * @author 飞花梦影
 * @date 2022-06-17 16:54:35
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class SecurityLogoutHandler extends SimpleUrlLogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		WebHelper.write(response, Result.ok());
	}
}