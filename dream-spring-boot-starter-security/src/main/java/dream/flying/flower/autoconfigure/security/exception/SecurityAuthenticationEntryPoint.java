package dream.flying.flower.autoconfigure.security.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.dream.enums.TipEnum;
import com.dream.result.Result;

import dream.framework.web.helper.WebHelpers;

/**
 * 匿名用户(token不存在、错误)，异常处理器
 *
 * @author 飞花梦影
 * @date 2023-08-09 17:37:51
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		authException.printStackTrace();
		WebHelpers.write(response, Result.error(TipEnum.TIP_AUTH_FAIL));
	}
}