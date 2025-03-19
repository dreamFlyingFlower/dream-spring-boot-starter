package dream.flying.flower.autoconfigure.logger.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.mapper.OperationLogMapper;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import lombok.RequiredArgsConstructor;

/**
 * 操作日志MyBatis存储实现类 实现日志的持久化存储，使用MyBatis-Plus进行数据库操作
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogEntity>
		implements OperationLogService {

}