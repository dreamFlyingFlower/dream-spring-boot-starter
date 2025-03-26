package dream.flying.flower.autoconfigure.excel.example.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.excel.example.dto.BomDTO;
import dream.flying.flower.autoconfigure.excel.example.entity.BomPO;
import dream.flying.flower.framework.web.convert.BaseConvert;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BomConvert extends BaseConvert<BomPO, BomDTO> {

	BomConvert INSTANCE = Mappers.getMapper(BomConvert.class);
}
