package dream.flying.flower.autoconfigure.web;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import dream.flying.flower.autoconfigure.web.CustomizerHelperAutoConfiguration.CustomizerHelperImportSelector;

/**
 * 将需要注入Spring组件的工具类注入再次注入到Spring Context中
 *
 * @author 飞花梦影
 * @date 2023-08-16 15:14:19
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@AutoConfiguration
@Import({ CustomizerHelperImportSelector.class })
public class CustomizerHelperAutoConfiguration {

	static class CustomizerHelperImportSelector implements ImportSelector {

		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider =
					new ClassPathScanningCandidateComponentProvider(true);
			Set<String> hashSet = new HashSet<>();
			classPathScanningCandidateComponentProvider.findCandidateComponents("dream.flying.flower.autoconfigure.web.helper")
					.forEach(beanDefinition -> hashSet.add(beanDefinition.getBeanClassName()));
			return hashSet.toArray(new String[hashSet.size()]);
		}
	}
}