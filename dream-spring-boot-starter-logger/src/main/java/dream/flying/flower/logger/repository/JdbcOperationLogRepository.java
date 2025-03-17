package dream.flying.flower.logger.repository;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import dream.flying.flower.logger.model.OperationLogModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JdbcOperationLogRepository implements OperationLogRepository {

	private final JdbcTemplate jdbcTemplate;

	@PostConstruct
	@Override
	public void checkAndCreateTable() {
		try {
			// 检查表是否存在
			String checkSql = "SELECT COUNT(1) FROM information_schema.tables WHERE table_name = 'sys_operation_log'";
			int count = jdbcTemplate.queryForInt(checkSql);

			if (count == 0) {
				// 读取建表SQL
				ClassPathResource resource = new ClassPathResource("sql/logger-schema.sql");
				String createTableSql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

				// 执行建表SQL
				jdbcTemplate.execute(createTableSql);
				log.info("Operation log table created successfully");
			}
		} catch (Exception e) {
			log.error("Failed to check/create operation log table", e);
			throw new RuntimeException("Failed to initialize operation log table", e);
		}
	}

	@Override
	public void save(OperationLogModel log) {
		String sql = "INSERT INTO sys_operation_log (trace_id, app_name, module, operation_type, "
				+ "operation_desc, method_name, class_name, package_name, request_method, request_url, "
				+ "request_params, request_body, response_body, success, error_msg, cost_time, "
				+ "client_ip, user_id, username) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			jdbcTemplate.update(sql, log.getTraceId(), log.getAppName(), log.getModule(), log.getOperationType(),
					log.getOperationDesc(), log.getMethodName(), log.getClassName(), log.getPackageName(),
					log.getRequestMethod(), log.getRequestUrl(), log.getRequestParams(), log.getRequestBody(),
					log.getResponseBody(), log.getSuccess(), log.getErrorMsg(), log.getCostTime(), log.getClientIp(),
					log.getUserId(), log.getUsername());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}