package dream.flying.flower.autoconfigure.web.properties;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Data;

/**
 * OpenApi配置
 *
 * @author 飞花梦影
 * @date 2023-08-14 10:38:55
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@ConfigurationProperties("dream.open-api")
@Data
public class OpenApiProperties {

	/** 全局请求参数配置 */
	private List<Parameter> globalParameters =
			Arrays.asList(new HeaderParameter().name("token").description("token校验").example("uuid32").required(true),
					new HeaderParameter().name("Authorization").description("Authorization校验").example("uuid32")
							.required(true));

	/** 开发相关信息 */
	private Info info;
}