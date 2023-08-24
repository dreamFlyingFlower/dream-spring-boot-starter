package dream.flying.flower.autoconfigure.security.helper;

import javax.servlet.http.HttpServletRequest;

import com.wy.digest.DigestTool;
import com.wy.lang.StrTool;

/**
 * Token 工具类
 *
 * @author 飞花梦影
 * @date 2023-07-08 17:09:20
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class TokenHelper {

	/**
	 * 生成 AccessToken
	 */
	public static String generator() {
		return DigestTool.uuid();
	}

	/**
	 * 获取 AccessToken
	 */
	public static String getAccessToken(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");
		if (StrTool.isBlank(accessToken)) {
			accessToken = request.getParameter("access_token");
		}

		return accessToken;
	}
}