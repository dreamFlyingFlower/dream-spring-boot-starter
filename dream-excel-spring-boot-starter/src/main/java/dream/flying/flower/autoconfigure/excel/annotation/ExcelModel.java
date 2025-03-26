package dream.flying.flower.autoconfigure.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 10:17:59
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelModel {

	String value();
}