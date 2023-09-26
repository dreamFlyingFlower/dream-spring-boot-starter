package dream.flying.flower.autoconfigure.excel.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wy.ConstDate;

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
public class BomDetailDTO implements Serializable {

	private static final long serialVersionUID = 592778813131560413L;

	@ApiModelProperty(value = "自增id")
	private Long id;

	@ApiModelProperty(value = "bom编号", required = true)
	private String bomCode;

	@ApiModelProperty(value = "产品编号", required = true)
	private String productCode;

	@ApiModelProperty(value = "bom版本,2021/11/12，U8系统对应新增字段")
	private String version;

	@ApiModelProperty(value = "bom层级", required = true)
	private Integer bomLevel;

	@ApiModelProperty(value = "bom行号", required = true)
	private Integer bomLineCode;

	@ApiModelProperty(value = "上级物料编码", required = true)
	private String parentMaterielCode;

	@ApiModelProperty(value = "父节点id", required = true)
	private Integer parentId;

	@ApiModelProperty(value = "设备扫描模式,字典值emes_scan_model")
	private String scanModel;

	@ApiModelProperty(value = "物料编码", required = true)
	private String materielCode;

	@ApiModelProperty(value = "物料名称", hidden = true)
	private String materielName;

	@ApiModelProperty(value = "物料规格", hidden = true)
	private String specification;

	@ApiModelProperty(value = "物料单位", hidden = true)
	private String measuringUnit;

	@ApiModelProperty(value = "物料使用数量", required = true)
	private BigDecimal materielNum;

	@ApiModelProperty(value = "基本数量")
	private BigDecimal basicNum;

	@ApiModelProperty(value = "基础数量")
	private BigDecimal baseNum;

	@ApiModelProperty(value = "物料位置", required = true)
	private String materielPosition;

	@ApiModelProperty(value = "物料类型 1:自制件 2：采购件 3：客供件", required = true)
	private String materielType;

	@ApiModelProperty(value = "物料说明")
	private String materielRemark;

	@ApiModelProperty(value = "物料产地/品牌")
	private String materielSource;

	@ApiModelProperty(value = "加工路线")
	private Integer madeId;

	@ApiModelProperty(value = "加工类型")
	private String madeType;

	@ApiModelProperty(value = "加工路线编码", hidden = true)
	private String madeCode;

	@ApiModelProperty(value = "工艺/工艺路线名称", hidden = true)
	private String madeName;

	@ApiModelProperty(value = "工艺路线级别", hidden = true)
	private String madeLevel;

	@ApiModelProperty(value = "工作中心id", required = true)
	private Integer workCenterId;

	@ApiModelProperty(value = "工作中心", hidden = true)
	private String workCenterName;

	@ApiModelProperty(value = "装配路线", required = true)
	private String assemble;

	@ApiModelProperty(value = "上料工序id", required = true)
	private Integer feedingProcessId;

	@ApiModelProperty(value = "上料工序编码", hidden = true)
	private String feedingProcessCode;

	@ApiModelProperty(value = "上料工序名称", hidden = true)
	private String feedingProcessName;

	@ApiModelProperty(value = "子节点列表", hidden = true)
	private List<BomDetailDTO> childrenList;

	@ApiModelProperty(value = "入库编码")
	private String halfStockCode;

	@ApiModelProperty(value = "入库数量")
	private BigDecimal inQuantity;

	@ApiModelProperty(value = "入库时间")
	private LocalDateTime putLibTime;

	@ApiModelProperty(value = "锁定数量")
	private BigDecimal lockQuantity;

	@ApiModelProperty(value = "上料验证标识,字典值emes_check_flag", required = true)
	private String feedingCheckFlag;

	@ApiModelProperty(hidden = true, value = "导入错误信息，后台返回")
	private String errorMsg;

	@ApiModelProperty(value = "替代料标识")
	private String newFlag;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;
}