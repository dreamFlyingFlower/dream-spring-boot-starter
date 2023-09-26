package dream.flying.flower.autoconfigure.excel.example.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.excel.example.dto.BomDetailDTO;
import dream.flying.flower.autoconfigure.excel.example.entity.BomDetailPO;
import dream.framework.web.convert.BaseConvert;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BomDetailConvert extends BaseConvert<BomDetailPO, BomDetailDTO> {

	BomDetailConvert INSTANCE = Mappers.getMapper(BomDetailConvert.class);
}
