package dream.flying.flower.autoconfigure.excel.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import dream.framework.mybatis.plus.entity.AbstractEntity;
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
@TableName("emes_basic_bom_item")
public class BomDetailPO extends AbstractEntity {

	private static final long serialVersionUID = 5309000039454555124L;

	@ApiModelProperty(value = "bom编号")
	private String bomCode;

	@ApiModelProperty(value = "产品编号")
	private String productCode;

	@ApiModelProperty(value = "bom层级")
	private Integer bomLevel;

	@ApiModelProperty(value = "bom行号")
	private Integer bomLineCode;

	@ApiModelProperty(value = "父节点id")
	private Integer parentId;

	@ApiModelProperty(value = "设备扫描模式，字典值emes_scan_model")
	private String scanModel;

	@ApiModelProperty(value = "物料编码")
	private String materielCode;

	@ApiModelProperty(value = "物料名称")
	@TableField(exist = false)
	private String materielName;

	@ApiModelProperty(value = "规格")
	@TableField(exist = false)
	private String specification;

	@ApiModelProperty(value = "计量单位")
	@TableField(exist = false)
	private String measuringUnit;

	@ApiModelProperty(value = "物料使用数量")
	private BigDecimal materielNum;

	@ApiModelProperty(value = "基本数量")
	private BigDecimal basicNum;

	@ApiModelProperty(value = "基础数量")
	private BigDecimal baseNum;

	@ApiModelProperty(value = "物料位置")
	private String materielPosition;

	@ApiModelProperty(value = "物料类型")
	private String materielType;

	@ApiModelProperty(value = "物料说明")
	private String materielRemark;

	@ApiModelProperty(value = "物料产地/品牌")
	private String materielSource;

	@ApiModelProperty(value = "加工路线")
	private Integer madeId;

	@ApiModelProperty(value = "工艺路线级别")
	private String madeLevel;

	@TableField(exist = false)
	@ApiModelProperty(value = "工艺/工艺路线编码")
	private String madeCode;

	@TableField(exist = false)
	@ApiModelProperty(value = "工艺/工艺路线名称")
	private String madeName;

	@ApiModelProperty(value = "工作中心id")
	private Integer workCenterId;

	@TableField(exist = false)
	@ApiModelProperty(value = "工作中心", hidden = true)
	private String workCenterName;

	@ApiModelProperty(value = "加工类型 2:工序组 3：工艺路线")
	private String madeType;

	@ApiModelProperty(value = "装配路线(字典值)")
	private String assemble;

	@ApiModelProperty(value = "上料工序id")
	private Integer feedingProcessId;

	@ApiModelProperty(value = "上料工序编码")
	@TableField(exist = false)
	private String feedingProcessCode;

	@ApiModelProperty(value = "上料工序名称")
	@TableField(exist = false)
	private String feedingProcessName;

	@ApiModelProperty(value = "上料验证标识,字典值emes_check_flag")
	private String feedingCheckFlag;

	/**
	 * 计划开工时间
	 */
	@TableField(exist = false)
	private LocalDateTime planStartTime;

	/**
	 * 计划完工时间
	 */
	@TableField(exist = false)
	private LocalDateTime planEndTime;

	/**
	 * 递归使用
	 */
	@TableField(exist = false)
	private List<BomDetailPO> children;
}