package dream.flying.flower.autoconfigure.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.dream.enums.TipEnum;
import com.dream.result.Result;

import dream.framework.web.helper.WebHelpers;

/**
 * Security登录失败
 *
 * @author 飞花梦影
 * @date 2022-06-17 16:52:36
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class SecurityFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		Result<?> result =
				Result.builder().code(TipEnum.TIP_INTERNAL_UNAUTHORIZED.getCode()).msg(exception.getMessage()).build();
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		WebHelpers.write(response, result);
	}
}