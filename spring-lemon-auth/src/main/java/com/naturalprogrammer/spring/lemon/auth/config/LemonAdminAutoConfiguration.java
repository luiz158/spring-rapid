package com.naturalprogrammer.spring.lemon.auth.config;

import com.naturalprogrammer.spring.lemon.auth.properties.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.bootstrap.AdminInitializer;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import io.github.vincemann.springrapid.acl.service.MockAuthService;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@ServiceConfig
@Slf4j
public class LemonAdminAutoConfiguration {

    public LemonAdminAutoConfiguration() {
        log.info("Created");
    }

    @Bean
    @ConditionalOnMissingBean(AdminInitializer.class)
    public AdminInitializer adminDatabaseDataInitializer(
            LemonService<?,?,?> lemonService,
            MockAuthService mockAuthService,
            UserDetailsService userDetailsService,
            LemonProperties lemonProperties){
        return new AdminInitializer(lemonService,mockAuthService,userDetailsService,lemonProperties);
    }
}
