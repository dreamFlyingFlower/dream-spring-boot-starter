package dream.flying.flower.autoconfigure.excel.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson2.JSON;
import com.dream.result.ResultException;

import dream.flying.flower.autoconfigure.excel.example.dto.BomDetailDTO;
import dream.flying.flower.autoconfigure.excel.example.query.BomDetailQuery;
import dream.flying.flower.autoconfigure.excel.handler.AbstractProcessData;
import lombok.extern.slf4j.Slf4j;

/**
 * bom明细导入导出模板
 * 
 * @Author: chenxj
 * @Date: 2021/12/17 10:35
 * @Description:
 */
@Service("bomDetailExportProcess")
@Slf4j
public class BomDetailExportProcess extends AbstractProcessData<BomDetailDTO> {

	/**
	 * 导入附带的参数
	 */
	private BomDetailQuery params;

	@Override
	public void setParams(Map<String, Object> params) {
		if (params != null && !params.isEmpty()) {
			this.params = JSON.parseObject(JSON.toJSONString(params), BomDetailQuery.class);
		} else {
			this.params = new BomDetailQuery();
		}
	}

	@Override
	public String getUqKey() {
		return this.params.getExcelCode() + this.params.getTimestamp();
	}

	@Override
	public Consumer<List<BomDetailDTO>> process() throws ResultException {
		return list -> {
			log.info("保存数据到db, data: " + list);
			List<BomDetailDTO> tempList = list.stream().peek(v -> {
				StringJoiner sb = new StringJoiner("，");
				if (list.stream().filter(v2 -> v.getMaterielCode().equals(v2.getMaterielCode())).count() > 1) {
					sb.add("导入数据中物料编码" + v.getMaterielCode() + "重复");
				}
				Date now = new Date();
				v.setCreateTime(now);
				v.setUpdateTime(now);
				v.setErrorMsg(sb.length() > 0 ? sb.toString() : "");
			}).collect(Collectors.toList());
			saveTemp(tempList.stream().map(v -> new DefaultTypedTuple<>(v, v.getErrorMsg().length() > 0 ? 1D : 0))
					.collect(Collectors.toSet()));

			Optional<BomDetailDTO> first =
					tempList.stream().filter(v -> v.getErrorMsg() != null && !v.getErrorMsg().isEmpty()).findFirst();
			if (!first.isPresent()) {
				List<String> processCodes = new ArrayList<>();
				// 根据母件编码(parentMaterielCode)建立层级关系,若上层有多个相同materielCode,更后面的为上级bom
				Map<String, List<BomDetailDTO>> mapMaterialCode2BomDetails = tempList.stream().peek(t -> {
					t.setBomCode(params.getBomCode());
					t.setProductCode(params.getProductCode());
					processCodes.add(t.getFeedingProcessCode());
				}).collect(Collectors.groupingBy(k -> k.getParentMaterielCode()));

				// 先存入顶层bom
				List<BomDetailDTO> tempBomDetailDTOs = mapMaterialCode2BomDetails.get("0");
				if (CollectionUtils.isEmpty(tempBomDetailDTOs)) {
					log.error("###:没有根物料数据,请检查excel文件");
					throw new ResultException("没有根物料数据,请检查excel文件");
				}
			} else {
				throw new ResultException(first.get().getErrorMsg());
			}
		};
	}

	@Override
	public Consumer<List<BomDetailDTO>> valid() throws ResultException {
		return list -> {
			log.debug("BomDetailDTO 验证");
			if (list == null) {
				throw new ResultException("数据不能为空");
			}
			list.forEach(v -> {
				if (ObjectUtils.isEmpty(v.getParentMaterielCode())) {
					throw new ResultException("母件编码不能为空");
				}
				if (ObjectUtils.isEmpty(v.getMaterielCode())) {
					throw new ResultException("子件编码不能为空");
				}
				if (ObjectUtils.isEmpty(v.getBomLevel())) {
					throw new ResultException("bom层级不能为空");
				}
				if (ObjectUtils.isEmpty(v.getBomLineCode())) {
					throw new ResultException("bom行号不能为空");
				}
				if (ObjectUtils.isEmpty(v.getMaterielType())) {
					throw new ResultException("子件属性不能为空");
				}
			});
		};
	}
}