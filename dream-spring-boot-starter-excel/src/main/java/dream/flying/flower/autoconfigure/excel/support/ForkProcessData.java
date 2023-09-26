package dream.flying.flower.autoconfigure.excel.support;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import com.wy.result.ResultException;

import dream.flying.flower.autoconfigure.excel.handler.ProcessData;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 10:19:31
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Slf4j
public class ForkProcessData<T> extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 3258787889024992080L;

	private static final long THRESHOLD = 2000L;

	private ProcessData<T> processData;

	private final int start;

	private final int end;

	private List<T> list;

	public ForkProcessData(ProcessData<T> processData, List<T> list) {
		this(processData, list, 0, list.size());
	}

	private ForkProcessData(ProcessData<T> processData, List<T> list, int start, int end) {
		this.processData = processData;
		this.start = start;
		this.end = end;
		this.list = list;
	}

	@Override
	protected Integer compute() {
		int length = this.end - this.start;
		if (length <= THRESHOLD) {
			return computeSequentially();
		}
		ForkProcessData<T> left =
				new ForkProcessData<>(this.processData, this.list, this.start, this.start + length / 2);
		left.fork();
		ForkProcessData<T> right =
				new ForkProcessData<>(this.processData, this.list, this.start + length / 2, this.end);
		Integer rightResult = right.compute();
		Integer leftResult = left.join();
		leftResult = Integer.valueOf(leftResult.intValue() + rightResult.intValue());
		return leftResult;
	}

	private Integer computeSequentially() {
		try {
			this.processData.valid().andThen(this.processData.process())
					.accept(this.list.subList(this.start, this.end));
		} catch (ResultException e) {
			log.error(e.getMessage());
			throw new ResultException(
					"操作失败" + e.getMessage() + ", 数据从" + (this.start + 1) + "到" + (this.end + 1) + "行");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResultException("操作失败,数据从" + (this.start + 1) + "到" + (this.end + 1) + "行");
		}
		return Integer.valueOf(this.end - this.start);
	}

	public static <T> Integer forkProcessData(ProcessData<T> processData, List<T> list) {
		ForkJoinTask<Integer> task = new ForkProcessData<>(processData, list);
		return new ForkJoinPool().invoke(task);
	}
}