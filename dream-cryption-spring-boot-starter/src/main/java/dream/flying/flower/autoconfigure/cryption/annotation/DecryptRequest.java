package dream.flying.flower.autoconfigure.cryption.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dream.flying.flower.digest.enums.CryptType;

/**
 * 接口安全,自动解密RequestBoby中的参数值
 *
 * @author 飞花梦影
 * @date 2022-12-20 14:42:12
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DecryptRequest {

	/**
	 * 密钥
	 */
	String value() default "";

	/**
	 * 解密方式
	 * 
	 * @return 加密方式
	 */
	CryptType cryptType() default CryptType.AES;
}