package dream.flying.flower.autoconfigure.captcha;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import dream.flying.flower.autoconfigure.captcha.properties.DreamCaptchaProperties;
import dream.flying.flower.helper.ConvertHepler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(DreamCaptchaProperties.class)
public class CaptchaAutoConfiguration {

	@Bean
	Producer captchaProducer(DreamCaptchaProperties dreamCaptchaProperties) throws IOException {
		Properties properties = null;
		if (null != dreamCaptchaProperties.getSource()) {
			properties = loadProperties(dreamCaptchaProperties.getSource());
		}

		if (null == properties) {
			properties = convert(dreamCaptchaProperties);
		} else {
			Properties dreamProperties = convert(dreamCaptchaProperties);
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				properties.merge(entry.getKey(), entry.getValue(), (o, n) -> dreamProperties.get(entry.getKey()));
			}
		}

		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}

	private Properties loadProperties(Resource resource) throws IOException {
		log.debug("captcha config file " + resource.getURL());
		Properties properties = new Properties();
		properties.load(resource.getInputStream());
		return properties;
	}

	private Properties convert(DreamCaptchaProperties dreamCaptchaProperties) {
		Properties properties = new Properties();
		properties.setProperty(Constants.KAPTCHA_BORDER, ConvertHepler.toStr(dreamCaptchaProperties.getBorder()));
		properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, dreamCaptchaProperties.getBorderColor());
		properties.setProperty(Constants.KAPTCHA_BORDER_THICKNESS, dreamCaptchaProperties.getBorderThickness());

		properties.setProperty(Constants.KAPTCHA_NOISE_COLOR, dreamCaptchaProperties.getNoiseColor());
		properties.setProperty(Constants.KAPTCHA_NOISE_IMPL,
				Objects.isNull(dreamCaptchaProperties.getNoiseImpl()) ? null
						: dreamCaptchaProperties.getNoiseImpl().getName());

		properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL,
				Objects.isNull(dreamCaptchaProperties.getObscurificatorImpl()) ? null
						: dreamCaptchaProperties.getObscurificatorImpl().getName());

		properties.setProperty(Constants.KAPTCHA_PRODUCER_IMPL,
				Objects.isNull(dreamCaptchaProperties.getProducerImpl()) ? null
						: dreamCaptchaProperties.getProducerImpl());

		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_IMPL,
				Objects.isNull(dreamCaptchaProperties.getTextproducerImpl()) ? null
						: dreamCaptchaProperties.getTextproducerImpl().getName());

		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING,
				dreamCaptchaProperties.getTextproducerCharString());
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH,
				ConvertHepler.toStr(dreamCaptchaProperties.getTextproducerCharLength()));

		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES,
				dreamCaptchaProperties.getTextproducerFontNames());
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR,
				dreamCaptchaProperties.getTextproducerFontColor());
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE,
				ConvertHepler.toStr(dreamCaptchaProperties.getTextproducerFontSize()));

		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE,
				dreamCaptchaProperties.getTextproducerCharSpace());

		properties.setProperty(Constants.KAPTCHA_WORDRENDERER_IMPL,
				Objects.isNull(dreamCaptchaProperties.getWordImpl()) ? null
						: dreamCaptchaProperties.getWordImpl().getName());

		properties.setProperty(Constants.KAPTCHA_BACKGROUND_IMPL,
				Objects.isNull(dreamCaptchaProperties.getBackgroundImpl()) ? null
						: dreamCaptchaProperties.getBackgroundImpl().getName());

		properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, dreamCaptchaProperties.getBackgroundClearFrom());
		properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_TO, dreamCaptchaProperties.getBackgroundClearTo());

		properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, ConvertHepler.toStr(dreamCaptchaProperties.getBorder()));
		properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, ConvertHepler.toStr(dreamCaptchaProperties.getBorder()));
		return properties;
	}
}