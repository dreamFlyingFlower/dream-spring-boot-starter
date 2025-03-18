package dream.flying.flower.logger.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Builder;
import lombok.Data;

/**
 * 操作日志实体类,用于存储系统操作日志信息的数据库实体
 * 
 * @author 飞花梦影
 * @date 2025-03-18 22:41:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@Builder
@TableName("sys_operation_log")
public class OperationLog {

	@TableId(type = IdType.AUTO)
	private Long id;

	private String traceId;

	private String appName;

	private String module;

	private String operationType;

	private String operationDesc;

	private String methodName;

	private String className;

	private String packageName;

	private String requestMethod;

	private String requestUrl;

	private String requestParams;

	private String requestBody;

	private String responseBody;

	private Boolean success;

	private String errorMsg;

	private Long costTime;

	private String clientIp;

	private String userId;

	private String username;

	private LocalDateTime createdTime;
}