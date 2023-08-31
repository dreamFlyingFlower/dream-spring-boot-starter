package dream.flying.flower.autoconfigure.web.crypto;

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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.util.ParameterizedTypeImpl;
import com.wy.digest.DigestHelper;
import com.wy.lang.StrHelper;
import com.wy.result.Result;
import com.wy.result.ResultException;

import dream.flying.flower.autoconfigure.web.properties.CryptoProperties;
import dream.framework.web.annotation.Encrypto;

/**
 * 参数加密
 *
 * @author 飞花梦影
 * @date 2022-12-20 14:39:46
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ControllerAdvice
@ConditionalOnMissingClass
@ConditionalOnProperty(prefix = "dream.crypto", value = "enabled", matchIfMissing = false)
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Result<?>> {

	private CryptoProperties cryptoProperties;

	public EncryptResponseBodyAdvice(CryptoProperties cryptoProperties) {
		this.cryptoProperties = cryptoProperties;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		ParameterizedTypeImpl genericParameterType = (ParameterizedTypeImpl) returnType.getGenericParameterType();

		// 如果直接是Result,则返回
		if (genericParameterType.getRawType() == Result.class && returnType.hasMethodAnnotation(Encrypto.class)) {
			return true;
		}

		if (genericParameterType.getRawType() != ResponseEntity.class) {
			return false;
		}

		// 如果是ResponseEntity<Result>
		for (Type type : genericParameterType.getActualTypeArguments()) {
			if (((ParameterizedTypeImpl) type).getRawType() == Result.class
			        && returnType.hasMethodAnnotation(Encrypto.class)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Result<?> beforeBodyWrite(Result<?> body, MethodParameter returnType, MediaType selectedContentType,
	        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
	        ServerHttpResponse response) {
		// 加密
		Object data = body.getData();

		// 如果data为空,直接返回
		if (data == null) {
			return body;
		}

		String dataText = JSON.toJSONString(data);

		// 如果data为空,直接返回
		if (StrHelper.isBlank(dataText)) {
			return body;
		}

		// 如果位数小于16,报错
		if (dataText.length() < 16) {
			throw new ResultException("加密失败,数据小于16位");
		}

		String encryptText = DigestHelper.AESEncrypt(cryptoProperties.getParamSecret(), dataText);
		return Result.result(body.getCode(), body.getMsg(), encryptText);
	}
}