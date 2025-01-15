package dream.flying.flower.autoconfigure.web;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import dream.flying.flower.enums.TipEnum;
import dream.flying.flower.enums.TipFormatEnum;
import dream.flying.flower.framework.core.constant.ConstConfigPrefix;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.result.Result;
import dream.flying.flower.result.ResultException;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一异常处理
 *
 * @author 飞花梦影
 * @date 2022-12-22 11:29:00
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@AutoConfiguration
@RestControllerAdvice
@ConditionalOnMissingClass
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = ConstConfigPrefix.AUTO_GLOBAL_EXCEPTION, value = ConstConfigPrefix.ENABLED,
		matchIfMissing = true)
public class GlobalExceptionAutoConfiguration {

	@ExceptionHandler(Throwable.class)
	public Result<?> handleException(Throwable throwable) {
		log.error(WebHelpers.getRequest().getRequestURL().toString(), throwable.getMessage());

		// 接口不存在异常
		if (throwable instanceof NoHandlerFoundException) {
			return Result.error(TipFormatEnum.TIP_REQUEST_URL_NOT_EXIST
					.getMsg(((NoHandlerFoundException) throwable).getRequestURL()));
		}

		// http请求方式不支持异常
		if (throwable instanceof HttpRequestMethodNotSupportedException) {
			return Result.error(TipFormatEnum.TIP_REQUEST_HTTP_METHOD_NOT_SUPPORTED
					.getMsg(((HttpRequestMethodNotSupportedException) throwable).getMethod()));
		}

		// 请求媒体类型异常
		if (throwable instanceof HttpMediaTypeNotSupportedException) {
			return Result.error(TipFormatEnum.TIP_REQUEST_MEDIA_TYPE_NOT_SUPPORTED
					.getMsg(((HttpMediaTypeNotSupportedException) throwable).getContentType().toString()));
		}

		// 必传参数为空异常
		if (throwable instanceof MissingPathVariableException) {
			return Result.error(TipFormatEnum.TIP_PARAM_REQUIRED_IS_NULL
					.getMsg(((MissingPathVariableException) throwable).getVariableName()));
		}
		if (throwable instanceof MissingServletRequestParameterException) {
			return Result.error(TipFormatEnum.TIP_PARAM_REQUIRED_IS_NULL
					.getMsg(((MissingServletRequestParameterException) throwable).getParameterName()));
		}
		if (throwable instanceof MissingServletRequestPartException) {
			return Result.error(TipFormatEnum.TIP_PARAM_REQUIRED_IS_NULL
					.getMsg(((MissingServletRequestPartException) throwable).getRequestPartName()));
		}

		// 参数类型不匹配异常
		if (throwable instanceof TypeMismatchException) {
			TypeMismatchException typeMismatchException = (TypeMismatchException) throwable;
			return Result.error("属性值:" + typeMismatchException.getPropertyName() + "-"
					+ typeMismatchException.getValue() + "类型不匹配");
		}

		// 参数绑定异常
		if (throwable instanceof ServletRequestBindingException) {
			return Result.error(TipEnum.TIP_PARAM);
		}

		// 参数未通过验证异常
		if (throwable instanceof BindException) {
			StringBuilder sb = new StringBuilder();
			// 解析原错误信息,封装后返回,此处返回非法的字段名称,原始值,错误信息
			for (FieldError error : ((BindException) throwable).getFieldErrors()) {
				sb.append("属性:" + error.getField() + "的值" + error.getRejectedValue() + "未通过校验" + ";");
			}
			return Result.error(sb.toString());
		}

		if (throwable instanceof ConstraintViolationException) {
			StringBuilder sb = new StringBuilder();
			// 解析原错误信息,封装后返回,此处返回非法的字段名称,原始值,错误信息
			for (ConstraintViolation<?> constraintViolation : ((ConstraintViolationException) throwable)
					.getConstraintViolations()) {
				sb.append("不可用的值[" + constraintViolation.getInvalidValue() + "]:" + constraintViolation.getMessage()
						+ ";");
			}
			return Result.error(sb.toString());
		}

		// 请求超时
		if (throwable instanceof AsyncRequestTimeoutException) {
			return Result.error(TipEnum.TIP_REQUEST_TIME_OUT);
		}

		// 服务器中断异常,当页面请求未返回数据而不停发送请求时,可能出现该异常
		if (throwable instanceof ClientAbortException) {
			return Result.error("服务请求中断");
		}

		if (throwable instanceof ResultException) {
			return Result.error(((ResultException) throwable).getMessage());
		}

		// 自定义异常界面
		// ModelAndView modelAndView = new ModelAndView();
		// modelAndView.setViewName("error.html");
		// modelAndView.addObject("msg", e.getMessage());
		// return modelAndView;

		return Result.error(throwable.getMessage());
	}
}