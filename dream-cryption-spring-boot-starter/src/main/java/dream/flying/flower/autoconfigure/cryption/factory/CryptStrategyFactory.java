package dream.flying.flower.autoconfigure.cryption.factory;

import dream.flying.flower.autoconfigure.cryption.strategy.AesCryptStrategy;
import dream.flying.flower.autoconfigure.cryption.strategy.CryptStrategy;
import dream.flying.flower.autoconfigure.cryption.strategy.DesCryptStrategy;
import dream.flying.flower.autoconfigure.cryption.strategy.RsaCryptStrategy;
import dream.flying.flower.digest.enums.CryptType;

/**
 * 加解密工厂类
 *
 * @author 飞花梦影
 * @date 2024-07-05 10:08:15
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CryptStrategyFactory {

	public static CryptStrategy getInstance(CryptType cryptType) {
		switch (cryptType) {
		case AES:
			return new AesCryptStrategy();
		case DES:
			return new DesCryptStrategy();
		case RSA:
			return new RsaCryptStrategy();
		default:
			throw new IllegalArgumentException("Unexpected value: " + cryptType);
		}
	}

	public static CryptStrategy getInstance(String cryptType) {
		switch (cryptType) {
		case "AES":
		case "aes":
			return new AesCryptStrategy();
		case "DES":
		case "des":
			return new DesCryptStrategy();
		case "RSA":
		case "rsa":
			return new RsaCryptStrategy();
		default:
			throw new IllegalArgumentException("Unexpected value: " + cryptType);
		}
	}
}