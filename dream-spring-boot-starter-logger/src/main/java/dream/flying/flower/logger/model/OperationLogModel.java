package dream.flying.flower.logger.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationLogModel {

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