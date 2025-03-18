//package dream.flying.flower.logger.logbook;
//
//import org.zalando.logbook.HttpRequest;
//import org.zalando.logbook.Precorrelation;
//import org.zalando.logbook.Sink;
//
///**
// * 指标监控集成Micrometer
// *
// * @author 飞花梦影
// * @date 2025-03-18 22:26:20
// * @git {@link https://github.com/dreamFlyingFlower}
// */
//public class MetricsSink implements Sink {
//
//	private final Counter requestCounter;
//
//	private final Timer requestTimer;
//
//	public MetricsSink(Sink delegate, MeterRegistry registry) {
//		this.requestCounter = registry.counter("logbook.requests");
//		this.requestTimer = registry.timer("logbook.request_duration");
//	}
//
//	@Override
//	public void write(Precorrelation precorrelation, HttpRequest request) {
//		requestCounter.increment();
//		Timer.Sample sample = Timer.start();
//
//		delegate.write(precorrelation, request);
//
//		sample.stop(requestTimer);
//	}
//}