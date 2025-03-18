package dream.flying.flower.logger.repository;

import org.springframework.stereotype.Repository;

import dream.flying.flower.logger.entity.OperationLog;
import dream.flying.flower.logger.mapper.OperationLogMapper;
import dream.flying.flower.logger.model.OperationLogModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志MyBatis存储实现类 实现日志的持久化存储，使用MyBatis-Plus进行数据库操作
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisOperationLogRepository implements OperationLogRepository {

	private final OperationLogMapper operationLogMapper;

	@Override
	public void save(OperationLogModel model) {
		OperationLog entity = OperationLog.builder()
				.traceId(model.getTraceId())
				.appName(model.getAppName())
				.module(model.getModule())
				.operationType(model.getOperationType())
				.operationDesc(model.getOperationDesc())
				.methodName(model.getMethodName())
				.className(model.getClassName())
				.packageName(model.getPackageName())
				.requestMethod(model.getRequestMethod())
				.requestUrl(model.getRequestUrl())
				.requestParams(model.getRequestParams())
				.requestBody(model.getRequestBody())
				.responseBody(model.getResponseBody())
				.success(model.getSuccess())
				.errorMsg(model.getErrorMsg())
				.costTime(model.getCostTime())
				.clientIp(model.getClientIp())
				.userId(model.getUserId())
				.username(model.getUsername())
				.createdTime(model.getCreatedTime())
				.build();

		operationLogMapper.insert(entity);
	}

	@Override
	public void checkAndCreateTable() {
		// 使用Flyway，不需要手动创建表
	}
}