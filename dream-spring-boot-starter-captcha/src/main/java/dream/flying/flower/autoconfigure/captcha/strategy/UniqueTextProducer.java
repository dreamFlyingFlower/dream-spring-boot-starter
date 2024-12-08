package dream.flying.flower.autoconfigure.captcha.strategy;

import java.util.Random;

import com.google.code.kaptcha.text.TextProducer;
import com.google.code.kaptcha.util.Configurable;

/**
 * 自定义唯一文本生成类
 *
 * @author 飞花梦影
 * @date 2024-12-05 23:28:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class UniqueTextProducer extends Configurable implements TextProducer {

	@Override
	public String getText() {
		int length = getConfig().getTextProducerCharLength();
		char[] chars = getConfig().getTextProducerCharString();
		Random rand = new Random();
		StringBuffer text = new StringBuffer();
		int i = 0;
		while (i < length) {
			char word = chars[rand.nextInt(chars.length)];
			if (text.indexOf(word + "") <= -1) {
				text.append(word);
				i++;
			}
		}
		return text.toString();
	}
}