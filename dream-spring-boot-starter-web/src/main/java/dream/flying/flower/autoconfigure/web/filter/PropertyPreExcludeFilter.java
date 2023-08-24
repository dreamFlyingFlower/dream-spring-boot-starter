package dream.flying.flower.autoconfigure.web.filter;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;

/**
 * 排除JSON敏感属性
 *
 * @author 飞花梦影
 * @date 2022-11-12 21:14:50
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class PropertyPreExcludeFilter extends SimplePropertyPreFilter {

	public PropertyPreExcludeFilter addExcludes(String... filters) {
		for (int i = 0; i < filters.length; i++) {
			this.getExcludes().add(filters[i]);
		}
		return this;
	}
}