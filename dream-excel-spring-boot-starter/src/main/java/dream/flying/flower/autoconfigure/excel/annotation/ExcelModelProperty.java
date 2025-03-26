package dream.flying.flower.autoconfigure.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 10:18:18
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelModelProperty {

	String value() default "";

	boolean hidden() default false;

	boolean id() default false;
}