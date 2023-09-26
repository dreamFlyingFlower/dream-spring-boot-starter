package dream.flying.flower.autoconfigure.excel.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import dream.framework.mybatis.plus.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Excel模板详情信息
 *
 * @author 飞花梦影
 * @date 2023-09-14 09:45:50
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@TableName("sys_excel_item")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelItemEntity extends AbstractEntity {

	private static final long serialVersionUID = -6923259088470716046L;

	private String excelCode;

	private Integer columnNo;

	private String fieldCode;

	private String fieldName;

	private String fieldType;

	private String validation;

	private Integer nullable;

	private String field;

	private String fieldDemo;
}