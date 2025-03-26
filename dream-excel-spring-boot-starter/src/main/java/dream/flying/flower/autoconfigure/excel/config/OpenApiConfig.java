package dream.flying.flower.autoconfigure.excel.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * OpenApi自动配置类,访问地址为ip:port/context-path/doc.html
 * 
 * @author 飞花梦影
 * @date 2022-12-05 13:47:21
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class OpenApiConfig {

	@Bean
	GroupedOpenApi excelApi() {
		return GroupedOpenApi.builder().group("Excel").packagesToScan("dream.flying.flower.autoconfigure.excel")
				.build();
	}

	@Bean
	@ConditionalOnMissingBean
	OpenAPI openApi() {
		return new OpenAPI().info(new Info().title("Excel操作").description("Excel文件导入导出")
				.contact(new Contact().name("飞花梦影").email("582822832@qq.com").url("https://github.com"))
				.version("v1.0"));
	}
}