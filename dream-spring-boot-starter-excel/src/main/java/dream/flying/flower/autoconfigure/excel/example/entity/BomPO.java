package dream.flying.flower.autoconfigure.excel.example.entity;

import java.time.LocalDate;

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
@TableName("emes_basic_bom")
public class BomPO extends AbstractEntity {

	private static final long serialVersionUID = -3088312620707320992L;

	@ApiModelProperty(value = "bom编号")
	private String bomCode;

	@ApiModelProperty(value = "产品编码")
	private String productCode;

	@TableField(exist = false)
	@ApiModelProperty(value = "产品名称")
	private String productName;

	@TableField(exist = false)
	@ApiModelProperty(value = "产品规格型号")
	private String productModel;

	@ApiModelProperty(value = "计量单位")
	private String measuremenUnit;

	@ApiModelProperty(value = "设备扫描模式，字典值emes_scan_model")
	private String scanModel;

	@ApiModelProperty(value = "bom状态")
	private String state;

	@ApiModelProperty(value = "版本代号")
	private String versionCode;

	@ApiModelProperty(value = "版本代号")
	private LocalDate versionDate;

	@ApiModelProperty(value = "客户编码")
	private String customerCode;

	@ApiModelProperty(value = "客户零件编码")
	private String customerPartCode;

	@ApiModelProperty(value = "项目编码")
	private String projectCode;

	@ApiModelProperty(value = "工艺路线编码")
	private Integer routingId;

	@TableField(exist = false)
	@ApiModelProperty(value = "工艺路线编码")
	private String routingCode;

	@TableField(exist = false)
	@ApiModelProperty(value = "工艺路线名称")
	private String routingName;

	@TableField(exist = false)
	@ApiModelProperty(value = "工艺路线级别")
	private String routingLevel;

	@TableField(exist = false)
	@ApiModelProperty(value = "工作中心id")
	private Integer workCenterId;

	@TableField(exist = false)
	@ApiModelProperty(value = "工作中心名称")
	private String workCenterName;

	@ApiModelProperty(value = "有效标识，0：停用；1：启用")
	private String isEnable;

	@ApiModelProperty(value = "是否来源外部导入，0：手动新增；1：外部U8系统导入")
	private String isFromImport;

	@ApiModelProperty(value = "拼版数量")
	private Integer arrayNum;
}