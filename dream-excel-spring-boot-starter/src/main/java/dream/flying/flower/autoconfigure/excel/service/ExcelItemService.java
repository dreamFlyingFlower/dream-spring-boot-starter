package dream.flying.flower.autoconfigure.excel.service;

import java.util.Optional;

import dream.flying.flower.autoconfigure.excel.dto.ExcelItemDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelItemEntity;
import dream.flying.flower.autoconfigure.excel.query.ExcelItemQuery;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 17:18:54
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public interface ExcelItemService extends BaseServices<ExcelItemEntity, ExcelItemDTO, ExcelItemQuery> {

	Optional<ExcelItemDTO> getByCode(String excelCode, String fieldCode);

	Optional<ExcelItemEntity> getByColumnNo(String excelCode, Integer columnNo);

	void deleteByExcelCode(String excelCode);

	void deleteByFieldCode(String excelCode, String fieldCode);

	void updateByFieldCode(String excelCode, ExcelItemDTO dto);
}