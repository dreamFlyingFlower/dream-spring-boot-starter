package dream.flying.flower.autoconfigure.web.crypto;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson2.util.ParameterizedTypeImpl;

import dream.flying.flower.digest.DigestHelper;
import dream.flying.flower.framework.core.annotation.EncryptParam;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.annotation.SecurityController;
import dream.flying.flower.framework.web.entity.BaseRequestEntity;
import dream.flying.flower.framework.web.properties.CryptoProperties;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * 参数加密,只能对RequestBody进行处理,只拦截含有SecurityController注解的Controller
 *
 * @author 飞花梦影
 * @date 2022-12-20 14:39:46
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ControllerAdvice(annotations = SecurityController.class)
@ConditionalOnMissingClass
@ConditionalOnProperty(prefix = "dream.crypto", value = "enabled", matchIfMissing = false)
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Result<?>> {

	private final CryptoProperties cryptoProperties;

	public EncryptResponseBodyAdvice(CryptoProperties cryptoProperties) {
		this.cryptoProperties = cryptoProperties;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// 获得泛型
		ParameterizedType parameterizedType = (ParameterizedType) returnType.getGenericParameterType();

		// 获得泛型
		System.out.println(parameterizedType.getActualTypeArguments()[0]);

		// 获得返回类
		if (returnType.getParameterType() == Result.class && returnType.hasMethodAnnotation(EncryptParam.class)) {
			return true;
		}

		if (returnType.getParameterType() != ResponseEntity.class) {
			return false;
		}

//		if (genericParameterType.getRawType() != ResponseEntity.class) {
//			return false;
//		}
//
//		// 如果是ResponseEntity<Result>
//		for (Type type : genericParameterType.getActualTypeArguments()) {
//			if (((ParameterizedTypeImpl) type).getRawType() == Result.class
//					&& returnType.hasMethodAnnotation(EncryptParam.class)) {
//				return true;
//			}
//		}

		return false;
	}

	@Override
	public Result<?> beforeBodyWrite(Result<?> body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		log.info("@@@加密前数据:{}", JsonHelpers.toJson(body));

		// 只对数据进行加密
		Object data = body.getData();
		if (data == null) {
			return body;
		}

		EncryptParam encrypto = returnType.getMethod().getAnnotation(EncryptParam.class);
		String secretKey = StrHelper.getDefault(encrypto.value(), cryptoProperties.getSecretKey());
		if (StrHelper.isBlank(secretKey)) {
			log.error("@@@未配置加密密钥,不进行加密!");
			return body;
		}

		// 如果是实体,并且继承了BaseRequestEntity,则放入时间戳
		if (data instanceof BaseRequestEntity) {
			((BaseRequestEntity) data).setRequestTime(System.currentTimeMillis());
		}

		String jsonData = JsonHelpers.toJson(data);

		return Result.result(body.getCode(), body.getMsg(), DigestHelper.aesEncrypt(secretKey, jsonData));
	}
}