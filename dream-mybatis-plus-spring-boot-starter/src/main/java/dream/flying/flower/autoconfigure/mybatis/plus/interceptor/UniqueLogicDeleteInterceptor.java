package dream.flying.flower.autoconfigure.mybatis.plus.interceptor;

import java.lang.reflect.Field;
import java.sql.SQLException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import dream.flying.flower.db.annotation.Unique;

/**
 * 解决唯一字段在数据库中新建索引后,逻辑删除情况下新增和删除索引不唯一的问题
 * 
 * 需要加入到MybatisPlusInterceptor中
 * 
 * 当前拦截器只能拦截根据ID删除的,不能拦截所有的
 * 
 * 要排除mybatis-plus自带的DefaultSqlInjector或重写DefaultSqlInjector#getMethodList,同时重写里面的
 * new Delete(), new DeleteByMap(), new DeleteById(), new DeleteBatchByIds(),
 *
 * @author 飞花梦影
 * @date 2025-09-28 16:05:50
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class UniqueLogicDeleteInterceptor implements InnerInterceptor {

	@Override
	public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
		if (parameter == null) {
			return;
		}

		// 获取实体类类型
		Class<?> entityClass = parameter.getClass();

		try {
			Field deletedField = getField(entityClass);
			if (null == deletedField) {
				return;
			}
			TableLogic tableLogic = deletedField.getAnnotation(TableLogic.class);
			deletedField.setAccessible(true);
			Object deletedValue = deletedField.get(parameter);
			if (!tableLogic.delval().equals(deletedValue)) {
				return;
			}

			for (Field field : entityClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Unique.class)) {
					processUniqueField(parameter, field);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Field getField(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (ArrayUtils.isEmpty(fields)) {
			return null;
		}
		for (Field field : fields) {
			if (field.isAnnotationPresent(TableLogic.class)) {
				return field;
			}
		}
		if (clazz.getSuperclass() != null) {
			return getField(clazz.getSuperclass());
		}
		return null;
	}

	private void processUniqueField(Object entity, Field uniqueField)
			throws IllegalArgumentException, IllegalAccessException {
		uniqueField.setAccessible(true);
		Object originalValue = uniqueField.get(entity);
		if (originalValue == null) {
			return;
		}

		String newValue = originalValue + "_deleted_" + System.currentTimeMillis();
		uniqueField.set(entity, newValue);
	}
}