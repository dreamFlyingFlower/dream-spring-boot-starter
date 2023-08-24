package dream.flying.flower.autoconfigure.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import dream.flying.flower.autoconfigure.security.CustomizerConfigAutoConfiguration.CustomizerConfigurationImportSelector;

/**
 * 初始化Bean
 *
 * @author 飞花梦影
 * @date 2022-12-22 14:08:59
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration
@Import({ CustomizerConfigurationImportSelector.class })
public class CustomizerConfigAutoConfiguration {

	static class CustomizerConfigurationImportSelector implements ImportSelector {

		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider =
					new ClassPathScanningCandidateComponentProvider(true);
			Set<String> hashSet = new HashSet<>();
			classPathScanningCandidateComponentProvider
					.findCandidateComponents("dream.flying.flower.autoconfigure.security.config")
					.forEach(beanDefinition -> hashSet.add(beanDefinition.getBeanClassName()));
			return hashSet.toArray(new String[hashSet.size()]);
		}
	}
}