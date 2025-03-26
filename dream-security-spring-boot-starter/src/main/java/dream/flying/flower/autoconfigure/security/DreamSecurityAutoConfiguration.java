package dream.flying.flower.autoconfigure.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import dream.flying.flower.autoconfigure.security.properties.SecurityProperties;

/**
 * 自动配置类
 * 
 * @author 飞花梦影
 * @date 2022-12-05 17:28:05
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "dream.security", value = "enabled", matchIfMissing = true)
@ConditionalOnMissingClass
public class DreamSecurityAutoConfiguration {

}