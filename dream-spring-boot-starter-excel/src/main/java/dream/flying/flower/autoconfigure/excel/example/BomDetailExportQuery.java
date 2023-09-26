package dream.flying.flower.autoconfigure.excel.example;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import dream.flying.flower.autoconfigure.excel.example.convert.BomDetailConvert;
import dream.flying.flower.autoconfigure.excel.example.dto.BomDetailDTO;
import dream.flying.flower.autoconfigure.excel.example.entity.BomDetailPO;
import dream.flying.flower.autoconfigure.excel.example.mapper.BomDetailMapper;
import dream.flying.flower.autoconfigure.excel.example.query.BomDetailQuery;
import dream.flying.flower.autoconfigure.excel.handler.AbstractQueryData;
import dream.flying.flower.autoconfigure.excel.handler.DataLimit;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BomDetailExportQuery extends AbstractQueryData<BomDetailDTO> {

	@Autowired
	private BomDetailMapper bomDetailMapper;

	private BomDetailQuery params;

	private BomDetailConvert bomDetailConvert;

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
	public Function<DataLimit, List<BomDetailDTO>> query() {
		return limit -> {
			log.debug("查询limit: " + limit);
			BomDetailQuery target = new BomDetailQuery();
			BeanUtils.copyProperties(params, target);
			int pageSize = limit.getEnd() - limit.getStart();
			int currentPage = limit.getStart() / pageSize + 1;
			target.setPageIndex(currentPage);
			target.setPageSize(pageSize);
			return bomDetailConvert.convertt(
					bomDetailMapper.selectPage(new Page<BomDetailPO>(currentPage, pageSize), null).getRecords());
		};
	}

	@Override
	public Function<List<BomDetailDTO>, List<BomDetailDTO>> process() {
		return list -> {
			log.debug("对数据进行处理");
			return list.stream().peek(v -> {
				// v.setIsRetailerName(WhetherStateEnum.getByCode(v.getIsRetailer()).getName());
			}).collect(Collectors.toList());
		};
	}

	@Override
	public Supplier<Integer> conut() {
		return () -> 1;
	}
}