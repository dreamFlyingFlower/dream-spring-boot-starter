package dream.flying.flower.autoconfigure.excel.query;

import dream.flying.flower.framework.web.query.AbstractQuery;
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
@Schema(description = "excel模板明细查询参数")
public class ExcelItemQuery extends AbstractQuery {

	private static final long serialVersionUID = 5689910560445759689L;

	@Schema(description = "excel模板编码", example = "EXTP-BAS-0001")
	private String excelCode;

	@Schema(defaultValue = "Excel详情字段")
	private String fieldCode;
}