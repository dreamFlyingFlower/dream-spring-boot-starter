package dream.flying.flower.autoconfigure.cryption.strategy;

import dream.flying.flower.digest.DigestHelper;

/**
 * Des加解密
 *
 * @author 飞花梦影
 * @date 2024-07-05 10:51:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class DesCryptStrategy implements CryptStrategy {

	@Override
	public String encrypt(byte[] secretKey, String content) {
		return DigestHelper.desEncrypt(secretKey, content);
	}

	@Override
	public String decrypt(byte[] secretKey, String content) {
		return DigestHelper.desDecrypt(secretKey, content);
	}
}