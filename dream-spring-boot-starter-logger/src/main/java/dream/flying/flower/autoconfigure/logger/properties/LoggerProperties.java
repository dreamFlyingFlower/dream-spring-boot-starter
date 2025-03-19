package dream.flying.flower.autoconfigure.logger.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.framework.core.constant.ConstConfigPrefix;
import lombok.Data;

/**
 * 日志配置属性类 用于配置日志记录的相关参数 包括是否启用、应用名称、扫描包路径、异步配置等
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:39:54
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@ConfigurationProperties(prefix = ConstConfigPrefix.AUTO_LOGGER)
public class LoggerProperties {

	/**
	 * 是否启用日志记录
	 */
	private boolean enabled = true;

	/**
	 * 应用名称
	 */
	private String appName;

	/**
	 * 需要扫描的包路径
	 */
	private List<String> scanPackages = new ArrayList<>();

	/**
	 * 是否异步记录日志
	 */
	private boolean async = true;

	/**
	 * 异步线程池核心线程数
	 */
	private int asyncCorePoolSize = 2;

	/**
	 * 异步线程池最大线程数
	 */
	private int asyncMaxPoolSize = 5;

	/**
	 * 异步线程池队列容量
	 */
	private int asyncQueueCapacity = 1000;

	/**
	 * 过滤规则
	 */
	private List<FilterRule> filters = new ArrayList<>();

	@Data
	public static class FilterRule {

		/**
		 * JSON 路径示例:$.user.password
		 */
		private String path;

		private Pattern pattern;

		private String replacement;
	}
}