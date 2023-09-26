package dream.flying.flower.autoconfigure.excel.support;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import com.wy.result.ResultException;

import dream.flying.flower.autoconfigure.excel.handler.DataLimit;
import dream.flying.flower.autoconfigure.excel.handler.QueryData;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 10:19:09
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Slf4j
public class ForkJoinData<T> extends RecursiveTask<List<T>> {

	private static final long serialVersionUID = -4145330286421654361L;

	private static final long THRESHOLD = 2000L;

	private QueryData<T> queryData;

	private final int start;

	private final int end;

	public ForkJoinData(QueryData<T> queryData) {
		this(queryData, 0, queryData.conut().get().intValue());
	}

	private ForkJoinData(QueryData<T> queryData, int start, int end) {
		this.queryData = queryData;
		this.start = start;
		this.end = end;
	}

	@Override
	protected List<T> compute() {
		int length = this.end - this.start;
		if (length <= THRESHOLD)
			return computeSequentially();
		ForkJoinData<T> left = new ForkJoinData<>(this.queryData, this.start, this.start + length / 2);
		left.fork();
		ForkJoinData<T> right = new ForkJoinData<>(this.queryData, this.start + length / 2, this.end);
		List<T> rightResult = right.compute();
		List<T> leftResult = left.join();
		leftResult.addAll(rightResult);
		return leftResult;
	}

	private List<T> computeSequentially() {
		try {
			return this.queryData.query().andThen(this.queryData.process())
					.apply(DataLimit.builder().start(this.start).end(this.end).build());
		} catch (ResultException e) {
			log.error(e.getMessage(), (Throwable) e);
			throw new ResultException(
					"操作失败" + e.getMessage() + ", 数据从" + (this.start + 1) + "到" + (this.end + 1) + "行");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ResultException("操作失败,数据从" + (this.start + 1) + "到" + (this.end + 1) + "行");
		}
	}

	public static <T> List<T> forkJoinSum(QueryData<T> queryData) {
		ForkJoinTask<List<T>> task = new ForkJoinData<>(queryData);
		return (new ForkJoinPool()).<List<T>>invoke(task);
	}
}