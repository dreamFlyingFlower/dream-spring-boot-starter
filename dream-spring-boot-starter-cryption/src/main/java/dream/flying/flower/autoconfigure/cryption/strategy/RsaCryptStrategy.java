package dream.flying.flower.autoconfigure.cryption.strategy;

import dream.flying.flower.digest.DigestHelper;

/**
 * Rsa非对称加解密
 *
 * @author 飞花梦影
 * @date 2024-07-05 10:51:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class RsaCryptStrategy implements CryptStrategy {

	@Override
	public String encrypt(String secretKey, String content) {
		return DigestHelper.rsaEncrypt(secretKey, content);
	}

	@Override
	public String encrypt(byte[] secretKey, String content) {
		return DigestHelper.rsaEncrypt(new String(secretKey), content);
	}

	@Override
	public String decrypt(String secretKey, String content) {
		return DigestHelper.rsaDecrypt(secretKey, content);
	}

	@Override
	public String decrypt(byte[] secretKey, String content) {
		return DigestHelper.rsaDecrypt(new String(secretKey), content);
	}
}