package dream.flying.flower.autoconfigure.cryption;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import dream.flying.flower.autoconfigure.cryption.annotation.CryptionController;
import dream.flying.flower.autoconfigure.cryption.annotation.EncryptResponse;
import dream.flying.flower.autoconfigure.cryption.api.GenerateKey;
import dream.flying.flower.autoconfigure.cryption.entity.BaseCryption;
import dream.flying.flower.autoconfigure.cryption.properties.EncryptResponseProperties;
import dream.flying.flower.autoconfigure.cryption.strategy.CryptContext;
import dream.flying.flower.collection.CollectionHelper;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.lang.StrHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 结果加密,只能对{@link ResponseBody}或含有{@link ResponseBody}的注解,同时含有{@link CryptionController}注解的类进行处理
 * 
 * 已单独测试完成
 * 
 * @author 飞花梦影
 * @date 2022-12-20 14:39:46
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@AutoConfiguration
@ConditionalOnMissingClass
@Import(GenerateKey.class)
@ControllerAdvice(annotations = CryptionController.class)
@EnableConfigurationProperties(EncryptResponseProperties.class)
@ConditionalOnProperty(prefix = "dream.encrypt-response", value = "enabled", matchIfMissing = true)
public class EncryptResponseBodyAutoConfiguration implements ResponseBodyAdvice<Object> {

	private final EncryptResponseProperties encryptResponseProperties;

	public EncryptResponseBodyAutoConfiguration(EncryptResponseProperties encryptResponseProperties) {
		this.encryptResponseProperties = encryptResponseProperties;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		Class<?> parameterType = returnType.getParameterType();
		if (void.class == parameterType || Void.class == parameterType) {
			return false;
		}

		if (!returnType.hasMethodAnnotation(EncryptResponse.class)) {
			return false;
		}

		List<Class<?>> encryptClass = encryptResponseProperties.getEncryptClass();

		if (CollectionHelper.isEmpty(encryptClass)) {
			return true;
		} else {
			return encryptClass.stream().anyMatch(t -> t == parameterType);
		}
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		if (body == null) {
			return body;
		}

		log.info("@@@加密前数据:{}", JsonHelpers.toString(body));

		// 获得泛型
		// ParameterizedType parameterizedType = (ParameterizedType)
		// returnType.getGenericParameterType();
		// System.out.println(parameterizedType.getActualTypeArguments()[0]);

		EncryptResponse encryptResponse = returnType.getMethod().getAnnotation(EncryptResponse.class);
		String secretKey = StrHelper.getDefault(encryptResponse.value(), encryptResponseProperties.getSecretKey());
		if (StrHelper.isBlank(secretKey)) {
			log.error("@@@未配置加密密钥,加密失败!");
			return body;
		}

		// FIXME 需要修改为支持多种结果集处理方式

		// 如果继承了BaseCryption,则放入时间戳
		if (body instanceof BaseCryption) {
			((BaseCryption) body).setRequestTime(System.currentTimeMillis());
		}

		return new CryptContext(encryptResponse.cryptType()).encrypt(secretKey, JsonHelpers.toString(body));
	}
}