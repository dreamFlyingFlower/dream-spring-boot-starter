package dream.flying.flower.autoconfigure.web;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncExecutionAspectSupport;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import dream.flying.flower.autoconfigure.web.properties.AsyncExecutorProperties;
import dream.flying.flower.autoconfigure.web.properties.AsyncTaskExecutorProperties;

/**
 * 初始化异步线程池
 *
 * @author 飞花梦影
 * @date 2022-12-21 14:25:23
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration
@EnableAsync
@EnableConfigurationProperties({ AsyncTaskExecutorProperties.class, AsyncExecutorProperties.class })
@ConditionalOnProperty(prefix = "dream.async-executor", value = "enabled", matchIfMissing = true)
@ConditionalOnMissingClass
public class AsyncExecutorAutoConfiguration {

	/**
	 * 使用该方式配置的线程池,会覆盖默认的线程池,{@link Async}中不需要指定额外的beanName
	 * 
	 * 当Project中有实现了{@link AsyncConfigurer}接口的Executor时,该默认Executor优先级低于实现类
	 * 
	 * @return 线程执行器
	 */
	@Bean(AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
	Executor defaultAsyncTaskExecutor(AsyncTaskExecutorProperties asyncTaskExecutorProperties) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 配置核心线程数
		executor.setCorePoolSize(asyncTaskExecutorProperties.getCorePoolSize());
		// 配置最大线程数
		executor.setMaxPoolSize(asyncTaskExecutorProperties.getMaxPoolSize());
		// 配置队列大小
		executor.setQueueCapacity(asyncTaskExecutorProperties.getQueueCapacity());
		// 配置空闲线程的最大时间,超过该时间,线程将被回收
		executor.setKeepAliveSeconds(asyncTaskExecutorProperties.getKeepAliveSeconds());
		// 配置线程名前缀
		executor.setThreadNamePrefix(asyncTaskExecutorProperties.getThreadNamePrefix());
		// 当线程池关闭时,定时任务是否立刻关闭,清空队列,而不等待他们执行完成,默认false,立刻关闭
		executor.setWaitForTasksToCompleteOnShutdown(asyncTaskExecutorProperties.getWaitForTasksToCompleteOnShutdown());
		// 当线程池要关闭时,仍有线程在执行,此时等待的最大时长
		executor.setAwaitTerminationSeconds(asyncTaskExecutorProperties.getAwaitTerminationSeconds());
		// 当线程池中的队列满时,设置拒绝策略
		executor.setRejectedExecutionHandler(asyncTaskExecutorProperties.getRejectedExecutionHandler());
		executor.initialize();
		return executor;
	}

	/**
	 * 使用该方式配置的线程池,可以配置多个,只需要使用的beanName不同即可.同时在{@link Async}中需要指定线程池的名称
	 * 
	 * 该方式配置的Executor不受{@link AsyncConfigurer}接口实现类的影响,只和beanName有关
	 * 
	 * @return 线程执行器
	 */
	@Bean
	@ConditionalOnMissingBean
	Executor defaultAsyncExecutor(AsyncExecutorProperties asyncExecutorProperties) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 配置核心线程数
		executor.setCorePoolSize(asyncExecutorProperties.getCorePoolSize());
		// 配置最大线程数
		executor.setMaxPoolSize(asyncExecutorProperties.getMaxPoolSize());
		// 配置队列大小
		executor.setQueueCapacity(asyncExecutorProperties.getQueueCapacity());
		// 配置空闲线程的最大时间,超过该时间,线程将被回收
		executor.setKeepAliveSeconds(asyncExecutorProperties.getKeepAliveSeconds());
		// 配置线程名前缀
		executor.setThreadNamePrefix(asyncExecutorProperties.getThreadNamePrefix());
		// 当线程池关闭时,定时任务是否立刻关闭,清空队列,而不等待他们执行完成,默认false,立刻关闭
		executor.setWaitForTasksToCompleteOnShutdown(asyncExecutorProperties.getWaitForTasksToCompleteOnShutdown());
		// 当线程池要关闭时,仍有线程在执行,此时等待的最大时长
		executor.setAwaitTerminationSeconds(asyncExecutorProperties.getAwaitTerminationSeconds());
		// 当线程池中的队列满时,设置拒绝策略
		executor.setRejectedExecutionHandler(asyncExecutorProperties.getRejectedExecutionHandler());
		executor.initialize();
		return executor;
	}
}