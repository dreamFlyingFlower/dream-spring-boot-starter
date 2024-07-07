package dream.flying.flower.autoconfigure.cryption.strategy;

import dream.flying.flower.autoconfigure.cryption.factory.CryptStrategyFactory;
import dream.flying.flower.digest.enums.CryptType;

/**
 * 加解密策略使用入口
 *
 * @author 飞花梦影
 * @date 2024-07-05 10:07:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CryptContext {

	private final CryptType cryptType;

	public CryptContext(CryptType cryptType) {
		this.cryptType = cryptType;
	}

	/**
	 * 加密
	 * 
	 * @param secretKey 密钥
	 * @param content 待加密内容
	 * @return 加密结果
	 */
	public String encrypt(String secretKey, String content) {
		return CryptStrategyFactory.getInstance(cryptType).encrypt(secretKey, content);

	}

	/**
	 * 加密
	 * 
	 * @param secretKey 密钥
	 * @param content 待加密内容
	 * @return 加密结果
	 */
	public String encrypt(byte[] secretKey, String content) {
		return CryptStrategyFactory.getInstance(cryptType).encrypt(secretKey, content);

	}

	/**
	 * 解密
	 * 
	 * @param secretKey 密钥
	 * @param content 待解密内容
	 * @return 解密结果
	 */
	public String decrypt(String secretKey, String content) {
		return CryptStrategyFactory.getInstance(cryptType).decrypt(secretKey, content);
	}

	/**
	 * 解密
	 * 
	 * @param secretKey 密钥
	 * @param content 待解密内容
	 * @return 解密结果
	 */
	public String decrypt(byte[] secretKey, String content) {
		return CryptStrategyFactory.getInstance(cryptType).decrypt(secretKey, content);
	}
}