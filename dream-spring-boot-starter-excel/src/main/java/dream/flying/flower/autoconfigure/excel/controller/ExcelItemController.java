package dream.flying.flower.autoconfigure.excel.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dream.result.Result;

import dream.flying.flower.autoconfigure.excel.dto.ExcelItemDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelItemEntity;
import dream.flying.flower.autoconfigure.excel.query.ExcelItemQuery;
import dream.flying.flower.autoconfigure.excel.service.ExcelItemService;
import dream.framework.web.controller.AbstractController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("v1/excelItem")
@Tag(name = "excel模板详情")
@AllArgsConstructor
public class ExcelItemController
		extends AbstractController<ExcelItemEntity, ExcelItemDTO, ExcelItemQuery, ExcelItemService> {

	@GetMapping({ "/{excelCode}/{fieldCode}" })
	@Operation(summary = "根据FieldCode删除")
	public Result<ExcelItemDTO> get(@Parameter(description = "Excel编码") @PathVariable String excelCode,
			@Parameter(description = "Field编码") @PathVariable String fieldCode) {
		return ok(this.baseService.getByCode(excelCode, fieldCode).orElse(null));
	}

	@PatchMapping({ "/{excelCode}/{fieldCode}" })
	@Operation(summary = "根据FieldCode编辑")
	public Result<?> update(@PathVariable String excelCode, @PathVariable String fieldCode,
			@RequestBody @Valid ExcelItemDTO dto) {
		dto.setFieldCode(fieldCode);
		this.baseService.updateByFieldCode(excelCode, dto);
		return ok();
	}

	@DeleteMapping({ "/{excelCode}/{fieldCode}" })
	@Operation(summary = "根据FieldCode删除")
	public Result<?> deleteByFieldCode(@PathVariable String excelCode, @PathVariable String fieldCode) {
		this.baseService.deleteByFieldCode(excelCode, fieldCode);
		return ok();
	}
}