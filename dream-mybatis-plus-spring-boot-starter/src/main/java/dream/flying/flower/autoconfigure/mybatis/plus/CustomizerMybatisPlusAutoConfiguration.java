package dream.flying.flower.autoconfigure.mybatis.plus;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import dream.flying.flower.autoconfigure.mybatis.plus.handler.DefaultMybatisPlusHandler;
import dream.flying.flower.autoconfigure.mybatis.plus.interceptor.UniqueLogicDeleteInterceptor;
import dream.flying.flower.autoconfigure.mybatis.plus.properties.DreamMybatisPlusProperties;

/**
 * 自定义MybatisPlus配置注入 TODO 事例,不写在当前项目中,新建starter写入
 *
 * @author 飞花梦影
 * @date 2024-11-07 14:31:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@AutoConfiguration(after = MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties(DreamMybatisPlusProperties.class)
public class CustomizerMybatisPlusAutoConfiguration {

	@Bean
	MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
		// 如果多数据源,此处不要指定,否则会影响分页
		mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		// 乐观锁
		mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
		// 防止全表更新与删除
		mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

		// 唯一字段删除逻辑,避免数据库索引重复
		mybatisPlusInterceptor.addInnerInterceptor(new UniqueLogicDeleteInterceptor());
		return mybatisPlusInterceptor;
	}

	@Bean
	@ConditionalOnMissingBean
	MetaObjectHandler metaObjectHandler(DreamMybatisPlusProperties dreamMybatisPlusProperties) {
		return new DefaultMybatisPlusHandler(dreamMybatisPlusProperties);
	}
}