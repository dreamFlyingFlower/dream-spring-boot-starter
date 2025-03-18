package dream.flying.flower.logger.repository;

import dream.flying.flower.logger.model.OperationLogModel;

public interface OperationLogRepository {

	void save(OperationLogModel log);

	void checkAndCreateTable();
}