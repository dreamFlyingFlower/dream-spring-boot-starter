package dream.flying.flower.logger.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import dream.flying.flower.logger.entity.HttpRequestLog;

/**
 * HTTP请求日志Mapper接口
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:41:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface HttpRequestLogMapper extends BaseMapper<HttpRequestLog> {
}