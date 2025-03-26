package dream.flying.flower.autoconfigure.excel.handler;

import java.util.Map;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-24 22:31:30
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public interface ParamHandler {

	void setParams(Map<String, Object> paramMap);

	String getUqKey();
}