package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.config.layers.config.ServiceConfig;
import io.github.vincemann.generic.crud.lib.config.layers.config.WebConfig;
import io.github.vincemann.generic.crud.lib.service.locator.CrudServiceLocator;
import io.github.vincemann.generic.crud.lib.service.locator.PackageScanningCrudServiceLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class CrudServiceLocatorConfig {

    @ConditionalOnMissingBean
    @Bean
    public CrudServiceLocator crudServiceLocator(){
        return new PackageScanningCrudServiceLocator();
    }
}