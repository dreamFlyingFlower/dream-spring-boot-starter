package dream.flying.flower.autoconfigure.excel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author 飞花梦影
 * @date 2022-12-05 17:28:05
 * @git {@link https://github.com/dreamFlyingFlower }
 */
// @AutoConfiguration
// @EnableConfigurationProperties()
// @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
// @ConditionalOnProperty(prefix = "dream.web-mvc", value = "enabled",
// matchIfMissing = true)
// @ConditionalOnMissingClass
@SpringBootApplication
@MapperScan(basePackages = { "dream.flying.flower.autoconfigure.excel.mapper",
		"dream.flying.flower.autoconfigure.excel.example.mapper" })
public class DreamExcelAutoConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(DreamExcelAutoConfiguration.class, args);
	}
}