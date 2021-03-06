package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.controller.NullCurrentUserIdProvider;
import io.github.vincemann.springrapid.core.controller.rapid.CurrentUserIdProvider;
import io.github.vincemann.springrapid.core.controller.rapid.EndpointsExposureContext;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.mergeUpdate.MergeUpdateStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.mergeUpdate.MergeUpdateStrategyImpl;
import io.github.vincemann.springrapid.core.controller.rapid.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.validationStrategy.ValidationStrategy;
import io.github.vincemann.springrapid.core.service.EndpointService;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SuppressWarnings("rawtypes")
@AutoConfigureAfter(DtoMapperAutoConfiguration.class)
@WebConfig
@Slf4j
public class RapidControllerAutoConfiguration {

    public RapidControllerAutoConfiguration() {
        log.info("Created");
    }

    @Value("${controller.idFetchingStrategy.idUrlParamKey:id}")
    private String idUrlParamKey;

    @Bean(name = "idUrlParamKey")
    public String idUrlParamKey(){
        return idUrlParamKey;
    }


    @Bean
    @ConditionalOnMissingBean(MergeUpdateStrategy.class)
    public MergeUpdateStrategy mergeUpdateStrategy(){
        return new MergeUpdateStrategyImpl();
    }

    /**
     * Define CurrentUserId Provider to use the Principal Feature in {@link io.github.vincemann.springrapid.core.controller.rapid.RapidController}
     * @see: @{@link io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo.Principal}
     */
    @Bean
    @ConditionalOnMissingBean(CurrentUserIdProvider.class)
    public CurrentUserIdProvider nullCurrentUserIdProvider(){
        return new NullCurrentUserIdProvider();
    }


    @ConditionalOnMissingBean(EndpointService.class)
    @Bean
    public EndpointService endpointService(@Autowired RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

    @ConditionalOnMissingBean(IdFetchingStrategy.class)
    @Bean
    public IdFetchingStrategy<Long> longIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy(idUrlParamKey());
    }

//    @ConditionalOnMissingBean(EndpointsExposureContext.class)
    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EndpointsExposureContext endpointsExposureContext(){
        return new EndpointsExposureContext();
    }

    @ConditionalOnMissingBean(ValidationStrategy.class)
    @Bean
    public ValidationStrategy validationStrategy(LocalValidatorFactoryBean localValidatorFactoryBean){
        //use spring validator, so dependency injection is supported
        return new JavaXValidationStrategy(localValidatorFactoryBean.getValidator());
    }


}
