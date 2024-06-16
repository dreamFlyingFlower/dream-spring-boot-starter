package dream.flying.flower.autoconfigure.excel.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dream.flying.flower.autoconfigure.excel.dto.ExcelDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelEntity;
import dream.flying.flower.framework.web.convert.BaseConvert;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExcelConvert extends BaseConvert<ExcelEntity, ExcelDTO> {

}