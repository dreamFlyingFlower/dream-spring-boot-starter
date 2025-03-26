package dream.flying.flower.autoconfigure.logger.query;

import java.time.LocalDateTime;

import dream.flying.flower.framework.web.query.AbstractQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 日志查询参数
 *
 * @author 飞花梦影
 * @date 2025-03-19 16:35:05
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogQuery extends AbstractQuery {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String traceId;

	private String appName;

	private String module;

	private String operationType;

	private String operationDesc;

	private String methodName;

	private String className;

	private String packageName;

	private String requestUrl;

	private String requestMethod;

	private String requestParams;

	private String requestBody;

	private String requestHeaders;

	private LocalDateTime requestTime;

	private LocalDateTime beginRequestTime;

	private LocalDateTime endRequestTime;

	private Integer responseStatus;

	private String responseBody;

	private String responseHeaders;

	private LocalDateTime responseTime;

	private LocalDateTime beginResponseTime;

	private LocalDateTime endResponseTime;

	private Long costTime;

	private Long minCostTime;

	private Long maxCostTime;

	private Integer success;

	private String errorMsg;

	private String clientIp;

	private String userId;

	private String username;

	private LocalDateTime createdTime;

	private LocalDateTime beginCreatedTime;

	private LocalDateTime endCreatedTime;
}