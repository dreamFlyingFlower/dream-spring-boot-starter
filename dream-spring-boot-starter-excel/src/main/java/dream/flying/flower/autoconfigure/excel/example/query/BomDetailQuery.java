package dream.flying.flower.autoconfigure.excel.example.query;

import dream.framework.web.query.AbstractQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BomDetailQuery extends AbstractQuery {

	private static final long serialVersionUID = -2689912713480785557L;

	@ApiModelProperty(value = "bom明细id")
	private Long bomId;

	@ApiModelProperty(value = "父节点id")
	private String parentId;

	@ApiModelProperty(value = "bom编号")
	private String bomCode;

	@ApiModelProperty(value = "bom层级")
	private String bomLevel;

	@ApiModelProperty(value = "bom编号")
	private String productCode;

	@ApiModelProperty(value = "物料编码")
	private String materielCode;

	@ApiModelProperty(value = "装配路线")
	private String assemble;

	@ApiModelProperty(value = "物料类型 1:自制件 2：采购件 3：客供件")
	private String materielType;

	@ApiModelProperty(value = "工艺级别")
	private String routingLevel;

	@ApiModelProperty(value = "产品/物料区别")
	private Integer bomType;

	@ApiModelProperty(value = "随工单code")
	private String orderNo;

	@ApiModelProperty(value = "上料验证标识")
	private String feedingCheckFlag;

	@ApiModelProperty(value = "计划编码")
	private String planNo;

	@ApiModelProperty(value = "上料工序名称")
	private String feedingProcessName;

	@ApiModelProperty(value = "工作中心id")
	private Integer workCenterId;

	@ApiModelProperty(value = "上料工序id")
	private Integer feedingProcessId;

	@ApiModelProperty(value = "班组id")
	private Integer workGroupId;

	@ApiModelProperty(value = "导入编码，导入excel使用")
	private String excelCode;

	@ApiModelProperty(value = "时间戳，请求的时间戳")
	private String timestamp;
}