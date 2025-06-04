package dream.flying.flower.autoconfigure.logger.service;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;

import com.baomidou.mybatisplus.extension.service.IService;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.LoggerProperties;
import dream.flying.flower.logger.Logger;

public interface OperationLogService extends IService<OperationLogEntity> {

	void save(ProceedingJoinPoint point, LoggerProperties loggerProperties, Object result, boolean success,
			String errorMsg, LocalDateTime requestTime, LocalDateTime now);

	void save(ProceedingJoinPoint point, Logger logger, LoggerProperties loggerProperties, Object result,
			boolean success, String errorMsg, LocalDateTime requestTime, LocalDateTime responseTime);
}