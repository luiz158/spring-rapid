package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@WebConfig
@EnableWebMvc
//@ComponentScan
@Slf4j
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    public WebMvcAutoConfiguration() {
        log.info("Created");
    }

    @Override
    public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
    }

}
