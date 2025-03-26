package dream.flying.flower.autoconfigure.excel.controller;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dream.flying.flower.autoconfigure.excel.dto.ExcelDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelEntity;
import dream.flying.flower.autoconfigure.excel.helper.ExcelAdapterHelpers;
import dream.flying.flower.autoconfigure.excel.manager.ExcelManager;
import dream.flying.flower.autoconfigure.excel.query.ExcelQuery;
import dream.flying.flower.autoconfigure.excel.query.ExcelTempQuery;
import dream.flying.flower.autoconfigure.excel.service.ExcelService;
import dream.flying.flower.framework.core.constant.ConstOffice;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.result.Result;
import dream.flying.flower.result.ResultException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("v1/excel")
@Tag(name = "excel")
@AllArgsConstructor
public class ExcelController extends AbstractController<ExcelEntity, ExcelDTO, ExcelQuery, ExcelService> {

	private ExcelManager excelManager;

	@GetMapping({ "{excelCode}" })
	@Operation(summary = "根据Excel编码获取详情", description = "根据Excel编码获取详情")
	public Result<ExcelDTO> getByCode(@PathVariable String excelCode) {
		return ok(this.baseService.getByCode(excelCode));
	}

	@DeleteMapping({ "{excelCode}" })
	@Operation(summary = "根据Excel编码删除", description = "根据Excel编码删除")
	public Result<?> deleteByCode(@PathVariable String excelCode) {
		this.baseService.deleteByCode(excelCode);
		return ok();
	}

	@PostMapping({ "exportTemplate/{excelCode}" })
	@Operation(summary = "导出模板")
	public Result<?> exportTemplate(@Parameter(description = "Excel编码") @PathVariable String excelCode,
			HttpServletResponse httpServletResponse) {
		ExcelDTO excelModel = Optional.of(this.baseService.getItemByCode(excelCode))
				.orElseThrow(() -> new ResultException("excel模板不存在"));
		ExcelAdapterHelpers.exportTemplate(excelModel, httpServletResponse);
		return ok();
	}

	@PostMapping(value = { "exportExcel/{excelCode}" }, produces = { "application/octet-stream" })
	@Operation(summary = "导出数据")
	public Result<?> exportExcel(@Parameter(description = "Excel编码") @PathVariable String excelCode,
			@RequestBody(required = false) Map<String, Object> params, HttpServletResponse httpServletResponse) {
		params.put("excelCode", excelCode);
		this.excelManager.exportExcel(excelCode, params, httpServletResponse);
		return ok();
	}

	@PostMapping({ "importExcel/{excelCode}" })
	@Operation(summary = "导入数据")
	public Result<?> importExcel(@Parameter(description = "Excel编码") @PathVariable String excelCode,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(required = false) Map<String, Object> params, HttpServletResponse httpServletResponse) {
		Assert.notNull(params.get("timestamp"), "timestamp");
		params.put("excelCode", excelCode);
		String name = file.getOriginalFilename();
		if (!name.toLowerCase().endsWith(ConstOffice.EXCEL_SUFFIX_XLS)
				&& !name.toLowerCase().endsWith(ConstOffice.EXCEL_SUFFIX_XLSX)) {
			throw new ResultException("excel文件格式错误");
		}
		this.excelManager.importExcel(excelCode, file, params, httpServletResponse);
		return ok();
	}

	@GetMapping({ "listTemp/{excelCode}" })
	@Operation(summary = "临时模板")
	public Result<?> listTemp(@Parameter(description = "Excel编码") @PathVariable String excelCode,
			ExcelTempQuery excelTempQuery) {
		excelTempQuery.setExcelCode(excelCode);
		Assert.notNull(excelTempQuery.getTimestamp(), "timestamp");
		return ok(this.excelManager.getExcelTempPage(excelTempQuery));
	}
}