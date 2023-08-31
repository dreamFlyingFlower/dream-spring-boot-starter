package dream.flying.flower.autoconfigure.security.helper;

import org.springframework.security.core.context.SecurityContextHolder;

import dream.flying.flower.autoconfigure.security.entity.SecurityUserDetails;

/**
 * 用户
 *
 * @author 飞花梦影
 * @date 2023-08-09 11:22:40
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public class SecurityHelpers {

	/**
	 * 获取用户信息
	 */
	public static SecurityUserDetails getUser() {
		SecurityUserDetails user;
		try {
			user = (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			return new SecurityUserDetails();
		}

		return user;
	}

	/**
	 * 获取用户ID
	 */
	public static Long getUserId() {
		return getUser().getId();
	}
}