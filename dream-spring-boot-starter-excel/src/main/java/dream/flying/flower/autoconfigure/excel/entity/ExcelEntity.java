package dream.flying.flower.autoconfigure.excel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dream.db.annotation.Unique;

import dream.framework.mybatis.plus.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Excel模板信息
 *
 * @author 飞花梦影
 * @date 2023-09-14 09:56:17
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@TableName("sys_excel")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelEntity extends AbstractEntity {

	private static final long serialVersionUID = 1929158550541493678L;

	@Unique
	private String excelCode;

	private String excelName;

	private String sheetName;

	private String objectClass;

	private String queryService;

	private String processService;
}