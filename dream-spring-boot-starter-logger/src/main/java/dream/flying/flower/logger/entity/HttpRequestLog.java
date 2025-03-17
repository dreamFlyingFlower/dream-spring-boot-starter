package dream.flying.flower.logger.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Builder;
import lombok.Data;

/**
 * HTTP请求日志实体类
 * 用于存储HTTP请求响应日志信息
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@Data
@Builder
@TableName("sys_http_request_log")
public class HttpRequestLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String traceId;
    private String appName;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private Long costTime;
    private String requestMethod;
    private String requestUrl;
    private String requestHeaders;
    private String requestBody;
    private Integer responseStatus;
    private String responseHeaders;
    private String responseBody;
    private String clientIp;
    private String errorMessage;
    private LocalDateTime createdTime;
} 