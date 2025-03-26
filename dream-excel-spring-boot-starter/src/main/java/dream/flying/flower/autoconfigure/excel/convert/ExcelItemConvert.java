package dream.flying.flower.autoconfigure.excel.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dream.flying.flower.autoconfigure.excel.dto.ExcelItemDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelItemEntity;
import dream.flying.flower.framework.web.convert.BaseConvert;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExcelItemConvert extends BaseConvert<ExcelItemEntity, ExcelItemDTO> {

}