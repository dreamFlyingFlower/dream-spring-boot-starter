package dream.flying.flower.autoconfigure.web.helper;

import java.util.Locale;

import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

/**
 * 国际化配置使用,自动配置类{@link MessageSourceAutoConfiguration},Spring组件中可以直接注入{@link MessageSource}使用
 * 
 * 在配置文件中配置spring.messages.beanname:i18n/messages,值是国际化文件的地址,必须在classpath下,最后一个表示文件名,messages可自定义.
 * 国际化配置文件一般分三段:文件名_语言缩写_国家缩写.properties,不带任何下划线后缀的是默认文件,即找不到本地化配置文件时使用,
 * 文件名都是相同的,只有后两段不同,可查看{@link java.util.Locale}
 * 
 * 若需要大量的国际化配置文件,放在classpath下是不明智的,可以重写{@link ResourceBundleMessageSource#doGetBundle},从数据库读取配置
 * 
 * @author 飞花梦影
 * @date 2021-01-28 17:28:20
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class MessageSourceHelpers {

	private static MessageSource messageSource;

	public MessageSourceHelpers(MessageSource messageSource) {
		MessageSourceHelpers.messageSource = messageSource;
	}

	public static String getMessage(String code) {
		return messageSource.getMessage(code, null, Locale.getDefault());
	}

	public static String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, Locale.getDefault());
	}

	public static String getMessage(String code, Locale locale, Object... args) {
		return messageSource.getMessage(code, args, locale);
	}

	public static String getMessage(String code, String defaultMessage) {
		return messageSource.getMessage(code, null, defaultMessage, Locale.getDefault());
	}

	public static String getMessage(String code, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, null, defaultMessage, locale);
	}

	public static String getMessage(String code, String defaultMessage, Object... args) {
		return messageSource.getMessage(code, args, defaultMessage, Locale.getDefault());
	}

	public static String getMessage(String code, String defaultMessage, Locale locale, Object... args) {
		return messageSource.getMessage(code, args, defaultMessage, locale);
	}
}