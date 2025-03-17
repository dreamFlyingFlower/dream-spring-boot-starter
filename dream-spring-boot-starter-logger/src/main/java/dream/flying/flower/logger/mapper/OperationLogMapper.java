package dream.flying.flower.logger.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import dream.flying.flower.logger.entity.OperationLog;

/**
 * 操作日志Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
} 