package dream.flying.flower.autoconfigure.web.properties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 默认线程池参数
 *
 * @author 飞花梦影
 * @date 2022-12-21 10:55:13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@ConfigurationProperties(prefix = "dream.async.task.executor")
public class AsyncTaskExecutorProperties {

	/** 配置核心线程数 */
	private Integer corePoolSize = 10;

	/** 配置最大线程数 */
	private Integer maxPoolSize = 20;

	/** 配置队列大小 */
	private Integer queueCapacity = 20;

	/** 配置空闲线程的最大时间,超过该时间,线程将被回收 */
	private Integer keepAliveSeconds = 60;

	/** 配置线程名前缀 */
	private String threadNamePrefix = "defaultAsyncTaskExecutor_";

	/** 当线程池关闭时,定时任务是否立刻关闭,清空队列,而不等待他们执行完成,默认false,立刻关闭 */
	private Boolean waitForTasksToCompleteOnShutdown = true;

	/** 当线程池要关闭时,仍有线程在执行,此时等待的最大时长 */
	private Integer awaitTerminationSeconds = 60;

	/** 当线程池中的队列满时,设置拒绝策略 */
	private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
}