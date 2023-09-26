package dream.flying.flower.autoconfigure.excel.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wy.result.ResultException;

import dream.flying.flower.autoconfigure.excel.convert.ExcelConvert;
import dream.flying.flower.autoconfigure.excel.convert.ExcelItemConvert;
import dream.flying.flower.autoconfigure.excel.dto.ExcelDTO;
import dream.flying.flower.autoconfigure.excel.dto.ExcelItemDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelEntity;
import dream.flying.flower.autoconfigure.excel.entity.ExcelItemEntity;
import dream.flying.flower.autoconfigure.excel.mapper.ExcelItemMapper;
import dream.flying.flower.autoconfigure.excel.mapper.ExcelMapper;
import dream.flying.flower.autoconfigure.excel.query.ExcelQuery;
import dream.flying.flower.autoconfigure.excel.service.ExcelItemService;
import dream.flying.flower.autoconfigure.excel.service.ExcelService;
import dream.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.AllArgsConstructor;

/**
 * 默认异步日志业务实现类
 *
 * @author 飞花梦影
 * @date 2022-11-14 10:29:09
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AllArgsConstructor
@Service
public class ExcelServiceImpl extends AbstractServiceImpl<ExcelEntity, ExcelDTO, ExcelQuery, ExcelConvert, ExcelMapper>
		implements ExcelService {

	private final ExcelItemService excelItemService;

	private final ExcelItemMapper excelItemMapper;

	private final ExcelItemConvert excelItemConvert;

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public void deleteByCode(String excelCode) {
		ExcelEntity excelEntity =
				this.getOne(new LambdaQueryWrapper<ExcelEntity>().eq(ExcelEntity::getExcelCode, excelCode));
		this.removeById(excelEntity.getId());
		this.excelItemService.deleteByExcelCode(excelCode);
	}

	@Override
	public ExcelDTO getByCode(String excelCode) {
		ExcelEntity excelEntity =
				this.getOne(new LambdaQueryWrapper<ExcelEntity>().eq(ExcelEntity::getExcelCode, excelCode));
		return baseConvert.convertt(excelEntity);
	}

	@Override
	public ExcelDTO getItemByCode(String excelCode) {
		ExcelDTO excelDTO = baseConvert.convertt(this.baseMapper
				.selectOne(new LambdaQueryWrapper<ExcelEntity>().eq(ExcelEntity::getExcelCode, excelCode)));

		List<ExcelItemEntity> excelItemEntities =
				this.excelItemMapper.selectList(new LambdaQueryWrapper<ExcelItemEntity>()
						.eq(ExcelItemEntity::getExcelCode, excelCode).orderByAsc(ExcelItemEntity::getColumnNo));
		if (CollectionUtils.isEmpty(excelItemEntities)) {
			throw new ResultException("ExcelCode为[%s]的Excel明细不存在,请先补充!", excelCode);
		}

		List<ExcelItemDTO> excelItemDTOs = excelItemConvert.convertt(excelItemEntities);
		excelDTO.setExcelItemDTOs(excelItemDTOs);

		return excelDTO;
	}
}