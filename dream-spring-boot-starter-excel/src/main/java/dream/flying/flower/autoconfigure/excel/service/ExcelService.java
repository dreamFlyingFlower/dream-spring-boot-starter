package dream.flying.flower.autoconfigure.excel.service;

import dream.flying.flower.autoconfigure.excel.dto.ExcelDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelEntity;
import dream.flying.flower.autoconfigure.excel.query.ExcelQuery;
import dream.framework.mybatis.plus.service.BaseServices;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 10:09:32
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
public interface ExcelService extends BaseServices<ExcelEntity, ExcelDTO, ExcelQuery> {

	void deleteByCode(String excelCode);

	ExcelDTO getByCode(String excelCode);

	ExcelDTO getItemByCode(String excelCode);
}