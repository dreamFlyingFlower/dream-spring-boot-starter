package dream.flying.flower.autoconfigure.excel.handler;

import java.util.List;
import java.util.function.Consumer;

import com.dream.result.ResultException;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 10:15:30
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@FunctionalInterface
public interface ProcessData<T> {

	Consumer<List<T>> process() throws ResultException;

	default Consumer<List<T>> valid() throws ResultException {
		return list -> {

		};
	}
}