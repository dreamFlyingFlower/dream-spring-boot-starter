package dream.flying.flower.autoconfigure.excel.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import dream.flying.flower.autoconfigure.excel.dto.ExcelDTO;
import dream.flying.flower.autoconfigure.excel.dto.ExcelItemDTO;
import dream.flying.flower.autoconfigure.excel.handler.ProcessData;
import dream.flying.flower.autoconfigure.excel.handler.QueryData;
import dream.flying.flower.autoconfigure.excel.support.ForkJoinData;
import dream.flying.flower.autoconfigure.excel.support.ForkProcessData;
import dream.flying.flower.collection.CollectionHelper;
import dream.flying.flower.collection.ListHelper;
import dream.flying.flower.framework.core.constant.ConstOffice;
import dream.flying.flower.framework.core.excel.ExcelContentHelpers;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.ResultException;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel工具类
 * 
 * @author 飞花梦影
 * @date 2021-01-28 17:28:20
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
public class ExcelAdapterHelpers {

	/**
	 * 导出模板Excel,默认使用新版本,且流会在写完数据后关闭
	 * 
	 * @param excelDTO 模板信息
	 * @param response HttpServletResponse
	 */
	public static void exportTemplate(ExcelDTO excelDTO, HttpServletResponse response) {
		ExcelContentHelpers.handlerExcelName(excelDTO.getExcelName(), response);
		try (OutputStream os = response.getOutputStream();) {
			exportTemplate(excelDTO, os);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("###Excel模板导出失败:{}", e.getMessage());
		}
	}

	/**
	 * 导出模板Excel,默认使用新版本
	 * 
	 * @param excelDTO 模板信息
	 * @param os 需自行关闭流
	 */
	public static <T> void exportTemplate(ExcelDTO excelDTO, OutputStream os) {
		valid(excelDTO);

		try (Workbook workbook = new SXSSFWorkbook();) {
			Sheet sheet =
					Optional.ofNullable(workbook.createSheet(excelDTO.getSheetName())).orElse(workbook.createSheet());
			sheet.setDefaultColumnWidth(20);
			// 标题行
			Row headerRow = sheet.createRow(0);
			// 示例行
			Row exampleRow = sheet.createRow(1);
			List<ExcelItemDTO> excelItemDTOs = excelDTO.getExcelItemDTOs();
			for (ExcelItemDTO excelItemDTO : excelItemDTOs) {
				headerRow.createCell(excelItemDTO.getColumnNo().intValue() - 1)
						.setCellValue(excelItemDTO.getFieldName());
				exampleRow.createCell(excelItemDTO.getColumnNo().intValue() - 1)
						.setCellValue(excelItemDTO.getFieldDemo());
			}
			workbook.write(os);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("###Excel模板导出失败:{}", e.getMessage());
		}
	}

	/**
	 * 数据导出
	 * 
	 * @param <T> 泛型
	 * @param queryData 数据查询
	 * @param excelDTO 模板信息
	 * @param response HttpServletResponse
	 */
	public static <T> void exportExcel(QueryData<T> queryData, ExcelDTO excelDTO, HttpServletResponse response) {
		List<T> list = ForkJoinData.forkJoinSum(queryData);
		exportExcel(list, excelDTO, response);
	}

	/**
	 * 数据导出
	 * 
	 * @param <T> 泛型
	 * @param queryData 数据查询
	 * @param excelDTO 模板信息
	 * @param os 需自行关闭流
	 */
	public static <T> void exportExcel(QueryData<T> queryData, ExcelDTO excelDTO, OutputStream os) throws Exception {
		List<T> list = ForkJoinData.forkJoinSum(queryData);
		exportExcel(list, excelDTO, os);
	}

	/**
	 * 数据导出
	 * 
	 * @param <T> 泛型
	 * @param list 数据
	 * @param excelDTO 模板信息
	 * @param response HttpServletResponse
	 */
	public static <T> void exportExcel(List<T> list, ExcelDTO excelDTO, HttpServletResponse response) {
		exportExcel(list, excelDTO, ConstOffice.EXCEL_SHEET_MAX_ROW_XLSX, response);
	}

	/**
	 * 数据导出
	 * 
	 * @param <T> 泛型
	 * @param list 数据
	 * @param excelDTO 模板信息
	 * @param os 需自行关闭流
	 * @throws Exception Exception
	 */
	public static <T> void exportExcel(List<T> list, ExcelDTO excelDTO, OutputStream os) throws Exception {
		exportExcel(list, excelDTO, ConstOffice.EXCEL_SHEET_MAX_ROW_XLSX, os);
	}

	/**
	 * 数据导出
	 * 
	 * @param <T> 泛型
	 * @param datas 数据
	 * @param excelDTO 模板信息
	 * @param sheetSize sheet页最大行数
	 * @param os 需自行关闭流
	 * @throws Exception
	 */
	private static <T> void exportExcel(List<T> datas, ExcelDTO excelDTO, int sheetSize, OutputStream os) {
		valid(excelDTO);

		sheetSize = Math.max(1, sheetSize);
		sheetSize = Math.min(sheetSize, ConstOffice.EXCEL_SHEET_MAX_ROW_XLSX);

		try (Workbook workbook = new SXSSFWorkbook();) {
			Sheet sheet =
					Optional.ofNullable(workbook.createSheet(excelDTO.getExcelName())).orElse(workbook.getSheetAt(0));
			sheet.setDefaultColumnWidth(20);
			if (CollectionHelper.isEmpty(datas)) {
				fillSheet(sheet, null, excelDTO, 0, 0);
			} else {
				fillSheet(sheet, datas, excelDTO, 0, datas.size() - 1);
			}
			workbook.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static <T> void exportExcel(List<T> list, ExcelDTO excelDTO, int sheetSize, HttpServletResponse response) {
		ExcelContentHelpers.handlerExcelName(excelDTO.getExcelName(), response);
		try (ServletOutputStream servletOutputStream = response.getOutputStream();) {
			exportExcel(list, excelDTO, sheetSize, (OutputStream) servletOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 数据导入
	 * 
	 * @param <T> 泛型
	 * @param processData 数据处理
	 * @param excelDTO 模板信息
	 * @param in InputStream
	 * @return 导入成功行数
	 */
	public static <T> Integer importExcel(ProcessData<T> processData, ExcelDTO excelDTO, InputStream in) {
		return ForkProcessData.forkProcessData(processData, importExcel(excelDTO, in));
	}

	public static <T> List<T> importExcel(ExcelDTO excelDTO, InputStream in) {
		valid(excelDTO);
		String sheetName = excelDTO.getExcelName();
		List<ExcelItemDTO> excelItemDTOs = excelDTO.getExcelItemDTOs();
		List<T> results = new ArrayList<>();
		try (Workbook workbook = new XSSFWorkbook(in);) {
			Sheet sheet = Optional.ofNullable(workbook.getSheet(sheetName)).orElse(workbook.getSheetAt(0));
			int rows = sheet.getLastRowNum() + 1;
			int realRows = 0;
			for (int i = 1; i <= rows; i++) {
				Row row = sheet.getRow(i);
				if (row != null && row.getLastCellNum() > 0) {
					realRows++;
					break;
				}
			}
			if (realRows <= 0) {
				throw new ResultException("Excel文件中没有任何数据");
			}
			Class<?> clazz = Class.forName(excelDTO.getObjectClass());
			for (int j = 1; j < rows; j++) {
				T entity = (T) clazz.newInstance();
				Row row = sheet.getRow(j);
				for (ExcelItemDTO v : excelItemDTOs) {
					Object content = row.getCell(v.getColumnNo().intValue());
					try {
						setFieldValueByName(v, content, entity);
					} catch (Exception e) {
						log.error("###导入Excel失败:" + e.getMessage());
						String start = "导入Excel失败";
						String end =
								"行号:" + j + ",列号 " + v.getColumnNo() + ",字段: " + v.getFieldName() + ",值: " + content;
						throw new ResultException(start + e.getMessage() + "," + end);
					}
				}
				results.add(entity);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	private static void setFieldValueByName(ExcelItemDTO excelItemDTO, Object fieldValue, Object object) {
		Field field = ReflectionUtils.findField(object.getClass(), excelItemDTO.getField());
		if (null == field) {
			throw new ResultException(
					"类[" + object.getClass().getSimpleName() + "]不存在映射字段[" + excelItemDTO.getField() + "]");
		}
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		String value = null;

		if (fieldValue != null && !String.valueOf(fieldValue).isEmpty()) {
			value = String.valueOf(fieldValue);
		}

		if (StrHelper.isBlank(value)) {
			if (0 == excelItemDTO.getNullable()) {
				throw new ResultException(excelItemDTO.getFieldName() + "值不能为空");
			}
			try {
				field.set(object, null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return;
		}

		String validation = excelItemDTO.getValidation();
		try {
			if (String.class == fieldType) {
				String real = value;
				if (real.length() > Integer.parseInt(validation))
					throw new ResultException("数据长度不能超过" + validation);
				field.set(object, real);
			} else if (int.class == fieldType || Integer.class == fieldType) {
				Integer real = Integer.valueOf(Integer.parseInt(value));
				if (value.length() > Integer.parseInt(validation))
					throw new ResultException("数据长度不能超过" + validation);
				field.set(object, real);
			} else if (long.class == fieldType || Long.class == fieldType) {
				Long real = Long.valueOf(Long.parseLong(value));
				if (value.length() > Integer.parseInt(validation))
					throw new ResultException("数据长度不能超过" + validation);
				field.set(object, real);
			} else if (short.class == fieldType || Short.class == fieldType) {
				Short real = Short.valueOf(Short.parseShort(value));
				if (value.length() > Integer.parseInt(validation))
					throw new ResultException("数据长度不能超过" + validation);
				field.set(object, real);
			} else if (float.class == fieldType || Float.class == fieldType) {
				Float real = Float.valueOf(Float.parseFloat(value));
				Integer pre = Integer.valueOf(Integer.parseInt(validation.split(",")[0]));
				Integer aft = Integer.valueOf(Integer.parseInt(validation.split(",")[1]));
				String[] strs = value.split("\\.");
				if (strs.length >= 2) {
					if (strs[0].length() > pre.intValue() || strs[1].length() > aft.intValue())
						throw new ResultException("数据长度不能超过" + validation);
				} else if (strs[0].length() > pre.intValue()) {
					throw new ResultException("数据长度不能超过" + validation);
				}
				field.set(object, real);
			} else if (double.class == fieldType || Double.class == fieldType) {
				Double real = Double.valueOf(Double.parseDouble(value));
				Integer pre = Integer.valueOf(Integer.parseInt(validation.split(",")[0]));
				Integer aft = Integer.valueOf(Integer.parseInt(validation.split(",")[1]));
				String[] strs = value.split("\\.");
				if (strs.length >= 2) {
					if (strs[0].length() > pre.intValue() || strs[1].length() > aft.intValue())
						throw new ResultException("数据长度不能超过" + validation);
				} else if (strs[0].length() > pre.intValue()) {
					throw new ResultException("数据长度不能超过" + validation);
				}
				field.set(object, real);
			} else if (BigDecimal.class == fieldType) {
				BigDecimal real = new BigDecimal(value);
				Integer pre = Integer.valueOf(Integer.parseInt(validation.split(",")[0]));
				Integer aft = Integer.valueOf(Integer.parseInt(validation.split(",")[1]));
				String[] strs = value.split("\\.");
				if (strs.length >= 2) {
					if (strs[0].length() > pre.intValue() || strs[1].length() > aft.intValue())
						throw new ResultException("数据长度不能超过" + validation);
				} else if (strs[0].length() > pre.intValue()) {
					throw new ResultException("数据长度不能超过" + validation);
				}
				field.set(object, real);
			} else if (Date.class == fieldType) {
				Date date = ((Cell) fieldValue).getDateCellValue();
				field.set(object, date);
			} else if (LocalDate.class == fieldType) {
				Date date = ((Cell) fieldValue).getDateCellValue();
				Instant instant = date.toInstant();
				ZoneId zone = ZoneId.systemDefault();
				LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
				LocalDate localDate = localDateTime.toLocalDate();
				field.set(object, localDate);
			} else if (LocalDateTime.class == fieldType) {
				Date date = ((Cell) fieldValue).getDateCellValue();
				Instant instant = date.toInstant();
				ZoneId zone = ZoneId.systemDefault();
				LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
				field.set(object, localDateTime);
			} else if (LocalTime.class == fieldType) {
				Date date = ((Cell) fieldValue).getDateCellValue();
				Instant instant = date.toInstant();
				ZoneId zone = ZoneId.systemDefault();
				LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
				LocalTime localTime = localDateTime.toLocalTime();
				field.set(object, localTime);
			} else {
				field.set(object, value);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static Row fillHeader(Sheet sheet, ExcelDTO excelDTO) {
		Row headRow = sheet.createRow(0);
		List<ExcelItemDTO> excelItemDTOs = excelDTO.getExcelItemDTOs();
		for (ExcelItemDTO excelItemDTO : excelItemDTOs) {
			Cell cellHeader = headRow.createCell(excelItemDTO.getColumnNo().intValue());
			cellHeader.setCellValue(excelItemDTO.getFieldName());
		}
		return headRow;
	}

	private static <T> void fillSheet(Sheet sheet, List<T> datas, ExcelDTO excelDTO, int firstIndex, int lastIndex) {
		if (ListHelper.isEmpty(datas)) {
			return;
		}
		fillHeader(sheet, excelDTO);
		if (!datas.get(0).getClass().getName().equals(excelDTO.getObjectClass())) {
			throw new ResultException("数据的类型不是" + excelDTO.getObjectClass());
		}
		Row row = null;
		int rowNo = 1;
		for (int index = firstIndex; index <= lastIndex; index++) {
			T item = datas.get(index);
			row = sheet.createRow(rowNo);
			for (ExcelItemDTO excelItemDTO : excelDTO.getExcelItemDTOs()) {
				setCellValue(row, excelItemDTO, item);
			}
			rowNo++;
		}
	}

	/**
	 * 给Cell单元格设置值
	 * 
	 * @param row 行
	 * @param excelItemDTO exel明细对象
	 * @param object 数据
	 */
	private static void setCellValue(Row row, ExcelItemDTO excelItemDTO, Object object) {
		Object fieldValue = getFieldValueByNameSequence(excelItemDTO.getField(), object);
		ExcelContentHelpers.setCellValue(row, excelItemDTO.getColumnNo().intValue(), fieldValue,
				excelItemDTO.getValidation());
	}

	private static Object getFieldValueByNameSequence(String fieldNameSequence, Object object) {
		Object value = null;
		String[] attributes = fieldNameSequence.split("\\.");
		if (attributes.length == 1) {
			value = getFieldValueByName(fieldNameSequence, object);
		} else {
			Object fieldObj = getFieldValueByName(attributes[0], object);
			String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
			value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
		}
		return value;
	}

	/**
	 * 递归向上查找对象中指定名称的字段值
	 * 
	 * @param fieldName 指定名称
	 * @param object 对象
	 * @return 字段值
	 */
	private static Object getFieldValueByName(String fieldName, Object object) {
		Field field = ReflectionUtils.findField(object.getClass(), fieldName);
		if (null == field) {
			throw new ResultException("类[%s]不存在字段[%s]", object.getClass().getName(), fieldName);
		}
		Object value = null;
		ReflectionUtils.makeAccessible(field);
		try {
			value = field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return value;
	}

	private static void valid(ExcelDTO excelDTO) {
		Assert.notNull(excelDTO, "Excel主属性不能为空");
		Assert.hasLength(excelDTO.getExcelName(), "Excel名称不能为空");
		Assert.hasLength(excelDTO.getObjectClass(), "Excel主属性实体类不能为空");
		Assert.notEmpty(excelDTO.getExcelItemDTOs(), "Excel明细属性不能为空");
		excelDTO.getExcelItemDTOs().stream().forEach(v -> {
			Assert.notNull(v.getColumnNo(), "Excel明细缺少columnNo");
			Assert.hasLength(v.getFieldCode(), "Excel明细fieldCode不能为空");
			Assert.hasLength(v.getFieldName(), "Excel明细fieldName不能为空");
			Assert.hasLength(v.getFieldType(), "Excel明细fieldType不能为空");
			Assert.hasLength(v.getValidation(), "Excel明细validation不能为空");
			Assert.hasLength(v.getField(), "Excel明细field不能为空");
			Assert.notNull(v.getNullable(), "Excel明细nullable不能为空");
		});
	}
}