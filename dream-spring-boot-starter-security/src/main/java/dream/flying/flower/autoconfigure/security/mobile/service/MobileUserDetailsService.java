package dream.flying.flower.autoconfigure.security.mobile.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 手机短信登录,UserDetailsService FIXME
 *
 * @author 飞花梦影
 * @date 2023-07-08 16:28:48
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public interface MobileUserDetailsService {

	/**
	 * 通过手机号加载用户信息
	 *
	 * @param mobile 手机号
	 * @return 用户信息
	 * @throws UsernameNotFoundException 不存在异常
	 */
	UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException;
}