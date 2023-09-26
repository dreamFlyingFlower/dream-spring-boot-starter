package dream.flying.flower.autoconfigure.excel.dto;

import java.util.List;

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
@Schema(description = "excel模板DTO")
public class ExcelDTO extends AbstractDTO {

	private static final long serialVersionUID = 1L;

	@Schema(description = "excel模板编码", example = "EXTP-BAS-0001")
	@NotBlank(message = "excel模板编码不能为空")
	@Size(max = 32, message = "长度不能超过32")
	private String excelCode;

	@Schema(description = "excel模板名称")
	@Size(max = 32, message = "长度不能超过32")
	private String excelName;

	@Schema(description = "sheet页名称")
	@Size(max = 32, message = "长度不能超过32")
	private String sheetName;

	@Schema(description = "映射对象", example = "UserDTO")
	@Size(max = 200, message = "长度不能超过200")
	private String objectClass;

	@Schema(description = "查询数据的服务,spring bean名称", example = "materielQuery")
	@NotBlank(message = "查询数据服务不能为空")
	@Size(max = 64, message = "长度不能茶瓯哦64")
	private String queryService;

	@Schema(description = "处理数据的服务,spring bean名称", example = "materielProcess")
	@NotBlank(message = "梳理数据的服务不能为空")
	@Size(max = 64, message = "长度不能超过64")
	private String processService;

	private List<ExcelItemDTO> excelItemDTOs;
}