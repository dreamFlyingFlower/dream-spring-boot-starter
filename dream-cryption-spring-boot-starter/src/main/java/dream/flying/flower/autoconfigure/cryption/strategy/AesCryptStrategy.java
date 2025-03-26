package dream.flying.flower.autoconfigure.cryption.strategy;

import dream.flying.flower.digest.DigestHelper;

/**
 * Aes加解密
 *
 * @author 飞花梦影
 * @date 2024-07-05 10:51:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class AesCryptStrategy implements CryptStrategy {

	@Override
	public String encrypt(byte[] secretKey, String content) {
		return DigestHelper.aesEncrypt(secretKey, content);
	}

	@Override
	public String decrypt(byte[] secretKey, String content) {
		return DigestHelper.aesDecrypt(secretKey, content);
	}
}