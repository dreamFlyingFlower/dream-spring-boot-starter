package dream.flying.flower.autoconfigure.security.cache;

/**
 * Redis Key管理
 *
 * @author 飞花梦影
 * @date 2023-07-12 09:46:28
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public interface RedisKeys {

	String KEY_FORMAT = "{%s}:{%s}:{%s}";

	/** Redis Key分隔符 */
	String KEY_DELIMITER = ":";

	/** 项目名 */
	String PROJECT_NAME = "dream-web-single";

	/** 模块名 */
	String MODULE_NAME = "cache";

	/**
	 * 构建验证码Key
	 * 
	 * @param key key
	 * @return 验证码key
	 */
	static String getCaptchaKey(String key) {
		return "sys:captcha:" + key;
	}

	/**
	 * 构建accessToken key
	 * 
	 * @param accessToken accessToken
	 * @return accessTokenKey
	 */
	static String getAccessTokenKey(String accessToken) {
		return buildModuleKey("sys", "access", accessToken);
	}

	/**
	 * 构建普通key
	 * 
	 * @param prefix key前缀
	 * @return 完整key
	 */
	static String buildKey(String prefix) {
		return String.format(KEY_FORMAT, PROJECT_NAME, MODULE_NAME, prefix);
	}

	/**
	 * 可变参数构建key
	 * 
	 * @param prefixs key数组
	 * @return 完整key
	 */
	static String buildKey(String... prefixs) {
		return new StringBuilder(PROJECT_NAME).append(KEY_DELIMITER).append(MODULE_NAME).append(KEY_DELIMITER)
				.toString() + String.join(KEY_DELIMITER, prefixs);
	}

	/**
	 * 构建模块key
	 * 
	 * @param moduleName 模块名
	 * @param prefix key前缀
	 * @return 完整key
	 */
	static String buildModuleKey(String moduleName, String prefix) {
		return String.format(KEY_FORMAT, PROJECT_NAME, moduleName, prefix);
	}

	/**
	 * 可变参数构建模块key
	 * 
	 * @param moduleName 模块名
	 * @param prefixs key数组
	 * @return 完整key
	 */
	static String buildModuleKey(String moduleName, String... prefixs) {
		return new StringBuilder(PROJECT_NAME).append(KEY_DELIMITER).append(moduleName).append(KEY_DELIMITER).toString()
				+ String.join(KEY_DELIMITER, prefixs);
	}
}