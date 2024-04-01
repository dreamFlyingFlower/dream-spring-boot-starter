package dream.flying.flower.autoconfigure.excel.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dream.enums.TipEnum;
import com.dream.result.ResultException;

import dream.flying.flower.autoconfigure.excel.convert.ExcelItemConvert;
import dream.flying.flower.autoconfigure.excel.dto.ExcelItemDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelItemEntity;
import dream.flying.flower.autoconfigure.excel.mapper.ExcelItemMapper;
import dream.flying.flower.autoconfigure.excel.query.ExcelItemQuery;
import dream.flying.flower.autoconfigure.excel.service.ExcelItemService;
import dream.framework.mybatis.plus.service.impl.AbstractServiceImpl;

/**
 *
 *
 * @author 飞花梦影
 * @date 2023-09-14 17:20:22
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Service
public class ExcelItemServiceImpl
		extends AbstractServiceImpl<ExcelItemEntity, ExcelItemDTO, ExcelItemQuery, ExcelItemConvert, ExcelItemMapper>
		implements ExcelItemService {

	@Override
	public void deleteByExcelCode(String excelCode) {
		this.baseMapper.delete(new LambdaQueryWrapper<ExcelItemEntity>().eq(ExcelItemEntity::getExcelCode, excelCode));
	}

	@Override
	public void deleteByFieldCode(String excelCode, String fieldCode) {
		ExcelItemDTO excelItemDTO = this.getByCode(excelCode, fieldCode).get();
		this.removeById(excelItemDTO.getId());
	}

	@Override
	public Optional<ExcelItemDTO> getByCode(String excelCode, String fieldCode) {
		return Optional
				.ofNullable(baseConvert.convertt(this.baseMapper.selectOne(new LambdaQueryWrapper<ExcelItemEntity>()
						.eq(ExcelItemEntity::getExcelCode, excelCode).eq(ExcelItemEntity::getFieldCode, fieldCode))));
	}

	@Override
	public Optional<ExcelItemEntity> getByColumnNo(String excelCode, Integer columnNo) {
		return Optional.ofNullable(this.baseMapper.selectOne(new LambdaQueryWrapper<ExcelItemEntity>()
				.eq(ExcelItemEntity::getExcelCode, excelCode).eq(ExcelItemEntity::getColumnNo, columnNo)));
	}

	@Override
	public ExcelItemEntity add(ExcelItemDTO dto) {
		this.getByCode(dto.getExcelCode(), dto.getFieldCode()).ifPresent(v -> {
			throw new ResultException(TipEnum.TIP_DB_DATA_EXIST);
		});
		this.getByColumnNo(dto.getExcelCode(), dto.getColumnNo()).ifPresent(v -> {
			throw new ResultException(TipEnum.TIP_DB_DATA_EXIST);
		});
		ExcelItemEntity excelItemEntity = baseConvert.convert(dto);
		this.save(excelItemEntity);
		return excelItemEntity;
	}

	@Override
	public void updateByFieldCode(String excelCode, ExcelItemDTO dto) {
		this.getByColumnNo(excelCode, dto.getColumnNo()).ifPresent(v -> {
			if (!dto.getFieldCode().equals(v.getFieldCode()))
				throw new ResultException(TipEnum.TIP_DB_DATA_NONE);
		});
		ExcelItemEntity excelItemEntity = baseConvert.convert(dto);
		excelItemEntity.setExcelCode(null);
		excelItemEntity.setFieldCode(null);
		this.updateById(excelItemEntity);
	}
}