package dream.flying.flower.autoconfigure.cryption.strategy;

/**
 * 加解密接口
 *
 * @author 飞花梦影
 * @date 2024-07-05 10:51:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface CryptStrategy {

	/**
	 * 加密
	 * 
	 * @param secretKey 密钥
	 * @param content 待加密内容
	 * @return 加密结果
	 */
	default String encrypt(String secretKey, String content) {
		return encrypt(secretKey.getBytes(), content);
	}

	/**
	 * 加密
	 * 
	 * @param secretKey 密钥
	 * @param content 待加密内容
	 * @return 加密结果
	 */
	String encrypt(byte[] secretKey, String content);

	/**
	 * 解密
	 * 
	 * @param secretKey 密钥
	 * @param content 待解密内容
	 * @return 解密结果
	 */
	default String decrypt(String secretKey, String content) {
		return decrypt(secretKey.getBytes(), content);
	}

	/**
	 * 解密
	 * 
	 * @param secretKey 密钥
	 * @param content 待解密内容
	 * @return 解密结果
	 */
	String decrypt(byte[] secretKey, String content);
}