package dream.flying.flower.autoconfigure.captcha.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.GimpyEngine;
import com.google.code.kaptcha.NoiseProducer;
import com.google.code.kaptcha.text.TextProducer;
import com.google.code.kaptcha.text.WordRenderer;

import dream.flying.flower.autoconfigure.captcha.strategy.LightNoise;
import dream.flying.flower.autoconfigure.captcha.strategy.RandomColorWordRenderer;
import dream.flying.flower.autoconfigure.captcha.strategy.Ripple;
import dream.flying.flower.autoconfigure.captcha.strategy.UniqueTextProducer;
import lombok.Data;

/**
 * 验证码配置
 *
 * @author 飞花梦影
 * @date 2024-07-30 09:14:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@ConfigurationProperties("dream.captcha")
public class DreamCaptchaProperties {

	/** properties文件地址 */
	private Resource source;

	/** 是否有边框 */
	private Boolean border = false;

	/** 边框颜色 */
	private String borderColor;

	/** 边框厚度 */
	private String borderThickness;

	/** 噪点颜色 */
	private String noiseColor;

	/** 噪点实现类 */
	private Class<? extends NoiseProducer> noiseImpl = LightNoise.class;

	/** 遮挡实现类 */
	private Class<? extends GimpyEngine> obscurificatorImpl = Ripple.class;

	/** 验证码实现类 */
	private String producerImpl;

	/** 文本生成器实现类 */
	private Class<? extends TextProducer> textproducerImpl = UniqueTextProducer.class;

	/** 验证码包含的字符 */
	private String textproducerCharString;

	/** 文本生成器字符长度 */
	private Integer textproducerCharLength = 4;

	/** 文本生成器字体 */
	private String textproducerFontNames;

	/** 文本生成器字体颜色 */
	private String textproducerFontColor;

	/** 字体大小 */
	private Integer textproducerFontSize = 30;

	/** 文本生成器字符空间 */
	private String textproducerCharSpace;

	/** 单词生成器实现类 */
	private Class<? extends WordRenderer> wordImpl = RandomColorWordRenderer.class;

	/** 背景实现类 */
	private Class<? extends BackgroundProducer> backgroundImpl;

	/** 背景清除起点 */
	private String backgroundClearFrom;

	/** 背景清除终点 */
	private String backgroundClearTo;

	/** 图片宽度 */
	private Integer imageWidth = 120;

	/** 图片高度 */
	private Integer imageHeight = 40;
}