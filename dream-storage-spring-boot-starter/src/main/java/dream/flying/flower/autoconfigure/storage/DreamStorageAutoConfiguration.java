package dream.flying.flower.autoconfigure.storage;

import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import dream.flying.flower.autoconfigure.storage.DreamStorageAutoConfiguration.StorageConfigurationImportSelector;
import dream.flying.flower.framework.storage.StorageManager;
import dream.flying.flower.framework.storage.StorageManagerCustomizer;
import dream.flying.flower.framework.storage.StorageManagerCustomizers;
import dream.flying.flower.framework.storage.config.StorageConfigurations;
import dream.flying.flower.framework.storage.enums.StorageType;
import dream.flying.flower.framework.storage.properties.StorageProperties;

/**
 * 存储自动配置,参照{@link CacheAutoConfiguration},默认使用本地存储.注意,自动配置不能被直接扫描,否则报错
 * 
 * @author 飞花梦影
 * @date 2023-08-11 14:41:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@AutoConfiguration
@Import({ StorageConfigurationImportSelector.class })
public class DreamStorageAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	StorageManagerCustomizers storageManagerCustomizers(ObjectProvider<StorageManagerCustomizer<?>> customizers) {
		return new StorageManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
	}

	@Bean
	StorageManagerValidator storageAutoConfigurationValidator(StorageProperties storageProperties,
			ObjectProvider<StorageManager> storageManager) {
		return new StorageManagerValidator(storageProperties, storageManager);
	}

	/**
	 * Bean used to validate that a StorageManager exists and provide a more
	 * meaningful exception.
	 */
	static class StorageManagerValidator implements InitializingBean {

		private final StorageProperties storageProperties;

		private final ObjectProvider<StorageManager> storageManager;

		StorageManagerValidator(StorageProperties storageProperties, ObjectProvider<StorageManager> storageManager) {
			this.storageProperties = storageProperties;
			this.storageManager = storageManager;
		}

		@Override
		public void afterPropertiesSet() {
			Assert.notNull(this.storageManager.getIfAvailable(),
					() -> "No storage manager could be auto-configured, check your configuration (storage type is '"
							+ this.storageProperties.getType() + "')");
		}
	}

	/**
	 * {@link ImportSelector} to add {@link StorageType} configuration classes.
	 */
	static class StorageConfigurationImportSelector implements ImportSelector {

		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			StorageType[] types = StorageType.values();
			String[] imports = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				imports[i] = StorageConfigurations.getConfigurationClass(types[i]);
			}
			return imports;
		}
	}
}