package dream.flying.flower.autoconfigure.web.crypto;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import dream.flying.flower.autoconfigure.web.properties.CryptoProperties;
import dream.flying.flower.digest.DigestHelper;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.annotation.Decrypto;
import dream.flying.flower.framework.web.entity.BaseRequestEntity;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.ResultException;
import lombok.SneakyThrows;

/**
 * 请求自动解密,只能对RequestBody进行处理,配合Decrypto使用
 *
 * @author 飞花梦影
 * @date 2022-12-20 14:57:47
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ControllerAdvice
@ConditionalOnMissingClass
@ConditionalOnProperty(prefix = "dream.crypto", value = "enabled", matchIfMissing = false)
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

	private CryptoProperties cryptoProperties;

	public DecryptRequestBodyAdvice(CryptoProperties cryptoProperties) {
		this.cryptoProperties = cryptoProperties;
	}

	/**
	 * 方法上有Decrypto注解的,进入此拦截器
	 * 
	 * @param methodParameter 方法参数对象
	 * @param targetType 参数的类型
	 * @param converterType 消息转换器
	 * @return true,进入,false,跳过
	 */
	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return methodParameter.hasMethodAnnotation(Decrypto.class);
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		return inputMessage;
	}

	/**
	 * 转换之后,执行此方法,解密,赋值
	 * 
	 * @param body spring解析完的参数
	 * @param inputMessage 输入参数
	 * @param parameter 参数对象
	 * @param targetType 参数类型
	 * @param converterType 消息转换类型
	 * @return 真实的参数
	 */
	@SneakyThrows
	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		HttpServletRequest request = WebHelpers.getRequest();
		// 获取数据
		ServletInputStream inputStream = request.getInputStream();
		String requestData = JsonHelpers.read(inputStream, String.class);

		if (StrHelper.isBlank(requestData)) {
			throw new ResultException("参数错误");
		}

		// 解密
		String decryptText = null;
		try {
			decryptText = DigestHelper.AESDecrypt(cryptoProperties.getParamSecret(), requestData);
		} catch (Exception e) {
			throw new ResultException("解密失败");
		}

		if (StrHelper.isBlank(decryptText)) {
			throw new ResultException("解密错误");
		}

		Object result = JsonHelpers.read(decryptText, body.getClass());

		// 强制所有实体类必须继承BaseRequestEntity,设置时间戳
		if (result instanceof BaseRequestEntity) {
			Long currentTimeMillis = ((BaseRequestEntity) result).getRequestTime();
			// 有效期 60秒
			long effective = 60 * 1000;

			long expire = System.currentTimeMillis() - currentTimeMillis;

			// 是否在有效期内
			if (Math.abs(expire) > effective) {
				throw new ResultException("时间戳不合法");
			}

			return result;
		} else {
			throw new ResultException(
					String.format("请求参数类型:%s 未继承:%s", result.getClass().getName(), BaseRequestEntity.class.getName()));
		}
	}

	/**
	 * 如果body为空,转为空对象
	 * 
	 * @param body spring解析完的参数
	 * @param inputMessage 输入参数
	 * @param parameter 参数对象
	 * @param targetType 参数类型
	 * @param converterType 消息转换类型
	 * @return 真实的参数
	 */
	@SneakyThrows
	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return Class.forName(targetType.getTypeName()).newInstance();
	}
}