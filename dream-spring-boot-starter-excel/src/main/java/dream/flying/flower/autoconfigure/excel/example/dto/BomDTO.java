package dream.flying.flower.autoconfigure.excel.example.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dream.ConstDate;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class BomDTO implements Serializable {

	private static final long serialVersionUID = 6641576013891708019L;

	@ApiModelProperty(value = "自增id")
	private Long id;

	@ApiModelProperty(value = "bom编号", hidden = true)
	private String bomCode;

	@ApiModelProperty(value = "产品编码", required = true)
	private String productCode;

	@ApiModelProperty(value = "产品名称", hidden = true)
	private String productName;

	@ApiModelProperty(value = "产品规格型号", hidden = true)
	private String productModel;

	@ApiModelProperty(value = "计量单位", required = true)
	private String measuremenUnit;

	@ApiModelProperty(value = "设备扫描模式 字典值emes_scan_model")
	private String scanModel;

	@ApiModelProperty(value = "bom状态", hidden = true)
	private String state;

	@ApiModelProperty(value = "版本代号", required = true)
	private String versionCode;

	@ApiModelProperty(value = "版本日期", required = true)
	private LocalDate versionDate;

	@ApiModelProperty(value = "客户编码")
	private String customerCode;

	@TableField(exist = false)
	@ApiModelProperty(value = "客户名称", hidden = true)
	private String customerName;

	@ApiModelProperty(value = "客户零件编码")
	private String customerPartCode;

	@ApiModelProperty(value = "项目编码")
	private String projectCode;

	@ApiModelProperty(value = "工艺路线编码", required = true)
	private Integer routingId;

	@ApiModelProperty(value = "工艺路线编码", hidden = true)
	private String routingCode;

	@ApiModelProperty(value = "工艺路线名称", hidden = true)
	private String routingName;

	@ApiModelProperty(value = "工艺路线级别", hidden = true)
	private String routingLevel;

	@ApiModelProperty(value = "工作中心id", hidden = true)
	private Integer workCenterId;

	@ApiModelProperty(value = "工作中心名称", hidden = true)
	private String workCenterName;

	@ApiModelProperty(value = "有效标识，0：无效；1：有效", hidden = true)
	private String isEnable;

	@ApiModelProperty(value = "是否来源外部导入，0：手动新增；1：外部U8系统导入", hidden = true)
	private String isFromImport;

	@ApiModelProperty(value = "bom明细列表", hidden = true)
	private List<BomDetailDTO> detailDTOList;

	@ApiModelProperty(value = "拼版数量")
	private String arrayNum;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;
}