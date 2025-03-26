package dream.flying.flower.autoconfigure.mybatis.plus.properties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.framework.core.constant.ConstConfigPrefix;
import lombok.Data;

/**
 * MyBatis-Plus配置
 *
 * @author 飞花梦影
 * @date 2023-08-11 09:22:51
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Data
@ConfigurationProperties(prefix = ConstConfigPrefix.AUTO_MYBATIS_PLUS)
public class DreamMybatisPlusProperties {

	/**
	 * 插入数据时自动填充的Java属性名以及默认值
	 */
	private Map<String, Object> insertFields = new HashMap<String, Object>() {

		private static final long serialVersionUID = 2687937192576260130L;

		{
			put("createTime", new Date());
			put("updateTime", new Date());
			put("version", 1);
			put("deleted", 0);
		}
	};

	/**
	 * 修改数据时自动更新的Java属性名以及默认值
	 */
	private Map<String, Object> updateFields = new HashMap<String, Object>() {

		private static final long serialVersionUID = 2687937192576260130L;

		{
			put("updateTime", new Date());
		}
	};
}