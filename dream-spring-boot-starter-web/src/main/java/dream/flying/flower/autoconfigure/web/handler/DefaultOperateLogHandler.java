package dream.flying.flower.autoconfigure.web.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import dream.flying.flower.enums.ResponseEnum;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.entity.OperateLog;
import dream.flying.flower.framework.web.handler.OperateLogHandler;
import dream.flying.flower.framework.web.helper.IpHelpers;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.logger.BusinessType;
import dream.flying.flower.logger.Logger;
import dream.flying.flower.logger.OperatorType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认异步日志业务实现类
 *
 * @author 飞花梦影
 * @date 2022-11-14 10:29:09
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@Async
@EnableAsync
public class DefaultOperateLogHandler implements OperateLogHandler {

	/** 排除敏感属性字段 */
	public static final List<String> EXCLUDE_PROPERTIES =
			new ArrayList<>(Arrays.asList("password", "oldPassword", "newPassword", "confirmPassword"));

	@Override
	public Object doAroundLogger(ProceedingJoinPoint joinPoint, Logger logger) {
		return doAspectAround(joinPoint, logger);
	}

	@Override
	public Object doAroundController(ProceedingJoinPoint joinPoint) {
		return doAspectAround(joinPoint, null);
	}

	protected Object doAspectAround(ProceedingJoinPoint joinPoint, Logger logger) {
		// 计时
		StopWatch stopWatch = new StopWatch(Thread.currentThread().getName());
		stopWatch.start();

		OperateLog operateLog = buildOperateLog(joinPoint);

		Object result = doMethod(joinPoint, operateLog, logger);

		handleTail(operateLog, stopWatch);

		return result;
	}

	/**
	 * 构建OperateLog
	 * 
	 * @param joinPoint 切面
	 * @return OperateLog
	 */
	protected OperateLog buildOperateLog(JoinPoint joinPoint) {
		HttpServletRequest request = WebHelpers.getRequest();
		return OperateLog.builder().beginTime(new Date()).operateIp(IpHelpers.getIp(request))
				.operateUrl(StrHelper.substring(request.getRequestURI(), 0, 255))
				.methodName(
						joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()")
				.requestMethod(request.getMethod()).build();
	}

	/**
	 * 执行真正的方法
	 * 
	 * @param joinPoint 切面
	 * @param operateLog 日志
	 * @param logger 日志注解
	 * @return 返回值
	 */
	protected Object doMethod(ProceedingJoinPoint joinPoint, OperateLog operateLog, Logger logger) {
		Object result = null;
		try {
			// 执行请求
			result = joinPoint.proceed();
			// 设置请求状态
			operateLog.setStatus(ResponseEnum.SUCCESS.ordinal());
			// 处理设置注解上的参数
			handleOtherInfo(joinPoint, logger, operateLog, result);
		} catch (Throwable ex) {
			// 记录本地异常日志
			log.error("###环绕通知异常:{}###", ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param joinPoint 切入点
	 * @param logger 日志
	 * @param operateLog 操作日志
	 * @param result 接口调用结果
	 * @throws Exception
	 */
	protected void handleOtherInfo(JoinPoint joinPoint, Logger logger, OperateLog operateLog, Object result)
			throws Exception {
		if (Objects.isNull(logger)) {
			handleMethodController(joinPoint, operateLog, result);
		} else {
			handleMethodLogger(joinPoint, logger, operateLog, result);
		}
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param joinPoint 切入点
	 * @param logger 日志
	 * @param operateLog 操作日志
	 * @param result 接口调用结果
	 * @throws Exception
	 */
	protected void handleMethodLogger(JoinPoint joinPoint, Logger logger, OperateLog operateLog, Object result)
			throws Exception {
		// 设置action动作
		operateLog.setBusinessType(logger.businessType().ordinal());
		// 设置标题
		operateLog.setModuleName(logger.value());
		// 设置操作人类别
		operateLog.setOperateType(logger.operatorType().ordinal());
		// 是否需要保存request,参数和值
		if (logger.isSaveRequestParams()) {
			// 获取参数的信息,传入到数据库中
			setRequestValue(joinPoint, operateLog);
		}
		// 是否需要保存response,参数和值
		if (logger.isSaveResponseResult() && Objects.nonNull(result)) {
			operateLog.setJsonResult(StrHelper.substring(JsonHelpers.toString(result), 0, 2000));
		}
	}

	/**
	 * 获取请求的参数,放到log中
	 * 
	 * @param joinPoint 切入点
	 * @param operateLog 操作日志
	 * @throws Exception 异常
	 */
	protected void setRequestValue(JoinPoint joinPoint, OperateLog operateLog) throws Exception {
		String requestMethod = operateLog.getRequestMethod();
		if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
			String params = argsArrayToString(joinPoint.getArgs());
			operateLog.setOperateParam(StrHelper.substring(params, 0, 2000));
		}
	}

	/**
	 * 参数拼装
	 * 
	 * @param params 参数列表
	 * @return 参数JSON字符串
	 */
	protected String argsArrayToString(Object[] params) {
		StringBuilder sb = new StringBuilder();
		if (ObjectUtils.isEmpty(params)) {
			return sb.toString();
		}
		for (Object param : params) {
			if (Objects.nonNull(param) && !isFilterObject(param)) {
				sb.append(JsonHelpers.toString(param, excludeProperties()).toString() + " ");
			}
		}
		return sb.toString().trim();
	}

	/**
	 * 判断是否需要过滤的对象
	 * 
	 * @param o 对象信息
	 * @return 如果是需要过滤的对象,则返回true;否则返回false
	 */
	@SuppressWarnings("rawtypes")
	protected boolean isFilterObject(final Object o) {
		Class<?> clazz = o.getClass();
		if (clazz.isArray()) {
			return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
		} else if (Collection.class.isAssignableFrom(clazz)) {
			Collection collection = (Collection) o;
			for (Object value : collection) {
				return value instanceof MultipartFile;
			}
		} else if (Map.class.isAssignableFrom(clazz)) {
			Map map = (Map) o;
			for (Object value : map.entrySet()) {
				Map.Entry entry = (Map.Entry) value;
				return entry.getValue() instanceof MultipartFile;
			}
		}
		return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
				|| o instanceof BindingResult;
	}

	/**
	 * 忽略敏感属性
	 */
	protected List<String> excludeProperties() {
		return EXCLUDE_PROPERTIES;
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param joinPoint 切入点
	 * @param operateLog 操作日志
	 * @param result 接口调用结果
	 * @throws Exception
	 */
	protected void handleMethodController(JoinPoint joinPoint, OperateLog operateLog, Object result) throws Exception {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		if (method.isAnnotationPresent(Operation.class)) {
			Operation apiOperation = method.getAnnotation(Operation.class);
			operateLog.setRemark(apiOperation.description());
		}

		String methodName = method.getName();
		// 设置标题
		operateLog.setModuleName(methodName);

		if (methodName.startsWith("add") || methodName.startsWith("insert") || methodName.startsWith("save")) {
			operateLog.setBusinessType(BusinessType.INSERT.ordinal());
		} else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
			operateLog.setBusinessType(BusinessType.DELETE.ordinal());
		} else if (methodName.startsWith("update") || methodName.startsWith("edit")
				|| methodName.startsWith("modify")) {
			operateLog.setBusinessType(BusinessType.UPDATE.ordinal());
		} else if (methodName.startsWith("query") || methodName.startsWith("get") || methodName.startsWith("list")
				|| methodName.startsWith("select")) {
			operateLog.setBusinessType(BusinessType.SELECT.ordinal());
		} else {
			operateLog.setBusinessType(BusinessType.OTHER.ordinal());
		}

		operateLog.setOperateParam(JsonHelpers.toString(getParameter(method, joinPoint.getArgs())));

		// 设置操作人类别
		operateLog.setOperateType(OperatorType.OTHER.ordinal());

		// 获取参数的信息,传入到数据库中
		setRequestValue(joinPoint, operateLog);
		operateLog.setJsonResult(StrHelper.substring(JsonHelpers.toString(result), 0, 2000));
	}

	/**
	 * 根据方法和传入的参数获取请求参数
	 */
	protected Object getParameter(Method method, Object[] args) {
		List<Object> argRets = new ArrayList<>();
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			// 跳过文件类型参数
			if (MultipartFile.class.isAssignableFrom(parameter.getType())) {
				continue;
			}
			// 将RequestBody注解修饰的参数作为请求参数
			if (parameter.isAnnotationPresent(RequestBody.class)) {
				argRets.add(args[i]);
			}
			// 将RequestParam注解修饰的参数作为请求参数
			if (parameter.isAnnotationPresent(RequestParam.class)) {
				RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
				Map<String, Object> map = new HashMap<>();
				String key = parameters[i].getName();
				if (StrHelper.isNotBlank(requestParam.value())) {
					key = requestParam.value();
				}
				map.put(key, args[i]);
				argRets.add(map);
			}
		}
		if (argRets.size() == 0) {
			return null;
		} else if (argRets.size() == 1) {
			return argRets.get(0);
		} else {
			return argRets;
		}
	}

	/**
	 * 设置相关数据
	 * 
	 * @param operateLog 日志
	 * @param stopWatch 计时
	 */
	protected void handleTail(OperateLog operateLog, StopWatch stopWatch) {
		stopWatch.stop();
		operateLog.setEndTime(new Date());
		operateLog.setOperateTime(stopWatch.getTotalTimeMillis());
		// 保存数据
		saveOperateLog(operateLog);
	}

	@Override
	public void doAfterThrowingLogger(JoinPoint joinPoint, Logger logger, Exception e) {
		doAspectThrowing(joinPoint, logger, e);
	}

	protected void doAspectThrowing(JoinPoint joinPoint, Logger logger, Exception e) {
		// 计时
		StopWatch stopWatch = new StopWatch(Thread.currentThread().getName());
		stopWatch.start();

		OperateLog operateLog = buildOperateLog(joinPoint);
		operateLog.setStatus(ResponseEnum.FAIL.ordinal());
		operateLog.setErrorMsg(StrHelper.substring(e.getMessage(), 0, 2000));

		try {
			// 处理设置注解上的参数
			handleOtherInfo(joinPoint, logger, operateLog, null);
		} catch (Exception ex) {
			// 记录本地异常日志
			log.error("###异常切面异常:{}###", ex.getMessage());
			ex.printStackTrace();
		}

		handleTail(operateLog, stopWatch);
	}

	@Async
	@Override
	public void saveOperateLog(OperateLog operateLog) {
		log.info(JsonHelpers.toString(operateLog));
	}
}