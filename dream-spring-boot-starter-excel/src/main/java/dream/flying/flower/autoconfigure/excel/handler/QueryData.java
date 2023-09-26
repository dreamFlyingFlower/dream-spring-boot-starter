package dream.flying.flower.autoconfigure.excel.handler;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 10:16:13
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public interface QueryData<T> {

	Function<DataLimit, List<T>> query();

	default Function<List<T>, List<T>> process() {
		return list -> list;
	}

	Supplier<Integer> conut();
}