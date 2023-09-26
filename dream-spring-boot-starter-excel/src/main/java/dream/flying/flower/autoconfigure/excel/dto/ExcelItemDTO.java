package dream.flying.flower.autoconfigure.excel.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import dream.framework.web.entity.AbstractDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "excel模板明细DTO")
public class ExcelItemDTO extends AbstractDTO {

	private static final long serialVersionUID = 1L;

	@Schema(description = "模板编码", example = "EXTP-BAS-0001")
	@NotBlank(message = "模板编码不能为空")
	private String excelCode;

	@Schema(description = "excel列号", example = "1")
	private Integer columnNo;

	@Schema(description = "字段编码", example = "userName")
	@NotBlank(message = "字段编码不能为空")
	@Size(max = 32, message = "长度不能超过32")
	private String fieldCode;

	@Schema(description = "字段名称", example = "用户名")
	@NotBlank(message = "字段名称不能为空")
	@Size(max = 32, message = "长度不能超过32")
	private String fieldName;

	@Schema(description = "字段类型", example = "String")
	@NotBlank(message = "字段类型不能为空")
	@Size(max = 16, message = "长度不能超过16")
	private String fieldType;

	@Schema(description = "校验内容", example = "32")
	@NotBlank(message = "校验内容不能为空")
	@Size(max = 16, message = "长度不能超过16")
	private String validation;

	@Schema(description = "是否可为null:0-否;1-是,默认1", example = "1")
	private Integer nullable;

	@Schema(description = "映射字段", example = "userName")
	@NotBlank(message = "映射字段不能为空")
	@Size(max = 32, message = "长度不能超过32")
	private String field;

	@Schema(description = "样例", example = "admin")
	@Size(max = 32, message = "长度不能超过32")
	private String fieldDemo;
}