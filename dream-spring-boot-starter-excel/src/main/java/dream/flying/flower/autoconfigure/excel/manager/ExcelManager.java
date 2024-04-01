package dream.flying.flower.autoconfigure.excel.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dream.enums.TipEnum;
import com.dream.result.ResultException;

import dream.flying.flower.autoconfigure.excel.dto.ExcelDTO;
import dream.flying.flower.autoconfigure.excel.dto.ExcelItemDTO;
import dream.flying.flower.autoconfigure.excel.entity.ExcelEntity;
import dream.flying.flower.autoconfigure.excel.handler.ProcessData;
import dream.flying.flower.autoconfigure.excel.handler.QueryData;
import dream.flying.flower.autoconfigure.excel.helper.ExcelAdapterHelpers;
import dream.flying.flower.autoconfigure.excel.query.ExcelTempQuery;
import dream.flying.flower.autoconfigure.excel.service.ExcelService;
import dream.framework.core.json.JsonHelpers;
import dream.framework.web.helper.SpringContextHelpers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2023-09-14 09:43:29
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Component
@AllArgsConstructor
@Slf4j
public class ExcelManager {

	private ExcelService excelService;

	public void exportExcel(String excelCode, Map<String, Object> params, HttpServletResponse httpServletResponse) {
		ExcelDTO v =
				Optional.of(this.excelService.getItemByCode(excelCode)).orElseThrow(() -> new ResultException("excel"));
		try {

			Object obj = SpringContextHelpers.getBean(v.getQueryService());
			if (!(QueryData.class.isAssignableFrom(obj.getClass()))) {
				throw new ResultException("查询类必须实现QueryData接口");
			}
			Method method = obj.getClass().getMethod("setParams", new Class[] { Map.class });
			method.invoke(obj, new Object[] { params });
			String timestamp = "timestamp";
			if (params.get(timestamp) != null) {
				Method listTemp = obj.getClass().getMethod("listTemp", new Class[0]);
				List<?> temps = (List<?>) listTemp.invoke(obj, new Object[0]);
				List<ExcelItemDTO> properties = new ArrayList<>();
				ExcelItemDTO property = new ExcelItemDTO();
				property.setColumnNo(Integer.valueOf(properties.size()));
				property.setFieldCode("errorMsg");
				property.setFieldName("");
				property.setFieldType("String");
				property.setValidation("1024");
				property.setNullable(1);
				property.setField("errorMsg");
				property.setFieldDemo("");
				properties.add(property);
				v.setExcelItemDTOs(properties);
				ExcelAdapterHelpers.exportExcel(temps, v, httpServletResponse);
			} else {
				ExcelAdapterHelpers.exportExcel((QueryData<?>) obj, v, httpServletResponse);
			}
		} catch (ResultException e) {
			log.error("{}, {}", e.getMessage(), e.getCause().getMessage());
			throw new ResultException(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ResultException(TipEnum.TIP_SYS_ERROR);
		}
	}

	public int importExcel(String excelCode, MultipartFile file, Map<String, Object> params,
			HttpServletResponse httpServletResponse) {
		ExcelDTO v =
				Optional.of(this.excelService.getItemByCode(excelCode)).orElseThrow(() -> new ResultException("excel"));
		try {
			Object obj = SpringContextHelpers.getBean(v.getProcessService());
			Method method = obj.getClass().getMethod("setParams", new Class[] { Map.class });
			method.invoke(obj, new Object[] { params });
			return ExcelAdapterHelpers.importExcel((ProcessData<?>) obj, v, file.getInputStream()).intValue();
		} catch (ResultException e) {
			log.error("{}, {}", e.getMessage(), e.getCause().getMessage());
			throw new ResultException(e.getCause().getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ResultException(TipEnum.TIP_SYS_ERROR);
		}
	}

	public void importExcel(String excelCode, Map<String, Object> params, NativeWebRequest nativeWebRequest) {
		ExcelEntity v = Optional
				.of(this.excelService
						.getOne(new LambdaQueryWrapper<ExcelEntity>().eq(ExcelEntity::getExcelCode, excelCode)))
				.orElseThrow(() -> new ResultException("excel模板不存在"));
		try {
			Object processService = SpringContextHelpers.getBean(v.getProcessService());
			Method setParams = processService.getClass().getMethod("setParams", new Class[] { Map.class });
			setParams.invoke(processService, new Object[] { params });
			Method save = processService.getClass().getMethod("save", new Class[0]);
			save.invoke(processService, new Object[0]);
		} catch (InvocationTargetException e) {
			Throwable throwable = e.getTargetException();
			if (throwable instanceof ResultException) {
				throw new ResultException(throwable.getMessage());
			}
			log.error(throwable.getMessage(), throwable);
			throw new ResultException(TipEnum.TIP_SYS_ERROR);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResultException(TipEnum.TIP_SYS_ERROR);
		}
	}

	public Page<?> getExcelTempPage(ExcelTempQuery params) {
		ExcelEntity v = Optional
				.of(this.excelService.getOne(
						new LambdaQueryWrapper<ExcelEntity>().eq(ExcelEntity::getExcelCode, params.getExcelCode())))
				.orElseThrow(() -> new ResultException("excel模板不存在"));
		try {
			Object queryService = SpringContextHelpers.getBean(v.getQueryService());
			Method setParams = queryService.getClass().getMethod("setParams", new Class[] { Map.class });
			setParams.invoke(queryService, new Object[] { JsonHelpers.readMap(params, new HashMap<>()) });
			Method method = queryService.getClass().getMethod("getPageTemp", new Class[] { int.class, int.class });
			int start = (params.getPageIndex().intValue() - 1) * params.getPageSize().intValue();
			int end = params.getPageSize().intValue();
			Object result = method.invoke(queryService, new Object[] { start, end });
			Map<String, Object> map = JsonHelpers.readMap(result);
			return new Page<List<Object>>(params.getPageIndex().intValue(), params.getPageSize().intValue(),
					Long.parseLong(map.get("count").toString())).setRecords((List) map.get("list"));
		} catch (ResultException e) {
			throw new ResultException(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ResultException(TipEnum.TIP_SYS_ERROR);
		}
	}
}