package dream.flying.flower.logger.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 * 可以配置模块名称、操作类型、操作描述等信息
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    String operationType() default "";
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否保存请求参数
     */
    boolean saveRequestData() default true;
    
    /**
     * 是否保存响应数据
     */
    boolean saveResponseData() default true;
} 