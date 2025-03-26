package dream.flying.flower.autoconfigure.cryption.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * 接口安全,配合{@link RequestBodyAdvice}使用,只拦截有该注解的Controller
 *
 * @author 飞花梦影
 * @date 2024-07-04 13:40:29
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CryptionController {

}