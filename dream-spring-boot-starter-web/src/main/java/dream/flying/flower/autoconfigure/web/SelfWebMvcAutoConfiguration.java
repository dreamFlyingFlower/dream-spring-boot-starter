package dream.flying.flower.autoconfigure.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.wy.ConstDate;

import dream.flying.flower.autoconfigure.web.properties.SelfWebMvcProperties;
import dream.framework.web.enums.SerializeLong;
import dream.framework.web.serial.LongToStringSerializer;

/**
 * 格式化LocalDateTime,不可继承 WebMvcConfigurationSupport,有可能会改变JSON序列化方式
 * 
 * 序列化方式需要借助原{@link JacksonProperties}配置
 *
 * @author 飞花梦影
 * @date 2022-12-05 17:28:05
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration
@EnableConfigurationProperties(SelfWebMvcProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "dream.web-mvc", value = "enabled", matchIfMissing = true)
@ConditionalOnMissingClass
public class SelfWebMvcAutoConfiguration implements WebMvcConfigurer {

	private SelfWebMvcProperties selfWebMvcProperties;

	private WebMvcProperties webMvcProperties;

	private ObjectMapper objectMapper;

	public SelfWebMvcAutoConfiguration(SelfWebMvcProperties selfWebMvcProperties, WebMvcProperties webMvcProperties,
			ObjectMapper objectMapper) {
		this.selfWebMvcProperties = selfWebMvcProperties;
		this.webMvcProperties = webMvcProperties;
		this.objectMapper = objectMapper;
	}

	// @Override
	// public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
	// {
	// converters.forEach(converter -> {
	// if (converter instanceof MappingJackson2HttpMessageConverter) {
	// ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter)
	// converter).getObjectMapper();
	//
	// if (dreamWebMvcProperties.getEnableLongToString()) {
	// convertLongToString(objectMapper);
	// }
	// // 时间格式化
	// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	// false);
	// objectMapper.setDateFormat(new SimpleDateFormat(ConstDate.DATETIME));
	// // 设置格式化内容
	// ((MappingJackson2HttpMessageConverter)
	// converter).setObjectMapper(objectMapper);
	// }
	// });
	// }

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ObjectMapper selfObjectMapper = new ObjectMapper();

		selfObjectMapper.setConfig(this.objectMapper.getSerializationConfig());
		selfObjectMapper.setConfig(this.objectMapper.getDeserializationConfig());

		if (selfWebMvcProperties.getEnableLocalDateTimeFormat()) {
			convertLocalDateTime(selfObjectMapper);
		}

		if (selfWebMvcProperties.getEnableLongToString()) {
			convertLongToString(selfObjectMapper);
		}

		selfObjectMapper.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(selfObjectMapper);
		converters.add(converter);
	}

	private void convertLocalDateTime(ObjectMapper selfObjectMapper) {
		JavaTimeModule javaTimeModule = new JavaTimeModule();

		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(
				Optional.ofNullable(webMvcProperties.getFormat().getDateTime()).orElse(ConstDate.DATETIME))));
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(
				Optional.ofNullable(webMvcProperties.getFormat().getDateTime()).orElse(ConstDate.DATETIME))));

		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter
				.ofPattern(Optional.ofNullable(webMvcProperties.getFormat().getDate()).orElse(ConstDate.DATE))));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter
				.ofPattern(Optional.ofNullable(webMvcProperties.getFormat().getDate()).orElse(ConstDate.DATE))));

		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter
				.ofPattern(Optional.ofNullable(webMvcProperties.getFormat().getTime()).orElse(ConstDate.TIME))));
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter
				.ofPattern(Optional.ofNullable(webMvcProperties.getFormat().getTime()).orElse(ConstDate.TIME))));

		selfObjectMapper.registerModule(javaTimeModule);
	}

	/**
	 * 将Long类型转为字符串,long超过9007199254740991L,即2^54-1会在前端js中丢失精度
	 * 
	 * @param selfObjectMapper
	 */
	private void convertLongToString(ObjectMapper selfObjectMapper) {
		SerializeLong serializeLong = selfWebMvcProperties.getSerialize().getSerializeLong();
		if (Objects.isNull(serializeLong) || SerializeLong.DEFAULT == serializeLong) {
			SimpleModule simpleModule = new SimpleModule();
			simpleModule.addSerializer(Long.class, new LongToStringSerializer());
			simpleModule.addSerializer(Long.TYPE, new LongToStringSerializer());
			simpleModule.addSerializer(long.class, new LongToStringSerializer());
			selfObjectMapper.registerModule(simpleModule);
		} else if (SerializeLong.ALWAYS == serializeLong) {
			SimpleModule simpleModule = new SimpleModule();
			simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
			simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
			simpleModule.addSerializer(long.class, ToStringSerializer.instance);
			selfObjectMapper.registerModule(simpleModule);
		}
	}
}