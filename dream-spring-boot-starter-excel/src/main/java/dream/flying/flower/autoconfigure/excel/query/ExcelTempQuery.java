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
@Schema(description = "excel临时数据DTO")
public class ExcelTempQuery extends AbstractQuery {

	private static final long serialVersionUID = 5361019158402478617L;

	@Schema(description = "excel模板编码", example = "EXTP-BAS-0001")
	private String excelCode;

	@Schema(description = "请求的时间戳")
	private String timestamp;

	@Schema(description = "创建人", hidden = true)
	private String creator;
}