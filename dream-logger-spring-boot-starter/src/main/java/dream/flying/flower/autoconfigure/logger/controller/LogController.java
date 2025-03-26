package dream.flying.flower.autoconfigure.logger.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.web.controller.BaseController;
import dream.flying.flower.result.Result;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogController implements BaseController {

	private OperationLogService operationLogService;

	@PostMapping("/operation")
	public Result<OperationLogEntity> createOperationLog(@RequestBody OperationLogEntity operationLogEntity) {
		operationLogService.save(operationLogEntity);
		return ok(operationLogEntity);
	}

	@GetMapping("/operation")
	public Result<List<OperationLogEntity>> getOperationLogs(OperationLogEntity operationLogEntity) {
		return ok(operationLogService.list(new QueryWrapper<OperationLogEntity>(operationLogEntity)));
	}
}