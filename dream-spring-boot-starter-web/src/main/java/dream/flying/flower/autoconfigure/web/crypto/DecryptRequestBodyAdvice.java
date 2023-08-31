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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.wy.digest.DigestHelper;
import com.wy.lang.StrHelper;
import com.wy.result.ResultException;

import dream.flying.flower.autoconfigure.web.properties.CryptoProperties;
import dream.framework.core.json.JsonHelpers;
import dream.framework.web.annotation.Decrypto;
import lombok.SneakyThrows;

/**
 * 请求自动解密
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
		// 获取request
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		if (servletRequestAttributes == null) {
			throw new ResultException("request错误");
		}

		HttpServletRequest request = servletRequestAttributes.getRequest();

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
			throw new ResultException("解密失败");
		}

		// 获取结果
		Object result = JsonHelpers.read(decryptText, body.getClass());

		// 返回解密之后的数据
		return result;
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
		String typeName = targetType.getTypeName();
		Class<?> bodyClass = Class.forName(typeName);
		return bodyClass.newInstance();
	}
}