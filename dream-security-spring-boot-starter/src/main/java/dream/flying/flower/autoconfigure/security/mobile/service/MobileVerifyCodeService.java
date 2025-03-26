package dream.flying.flower.autoconfigure.security.mobile.service;

/**
 * 手机短信登录,验证码效验 FIXME
 *
 * @author 飞花梦影
 * @date 2023-07-08 16:29:00
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public interface MobileVerifyCodeService {

	boolean verifyCode(String mobile, String code);
}