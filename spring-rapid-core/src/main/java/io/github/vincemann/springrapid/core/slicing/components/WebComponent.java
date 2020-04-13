package io.github.vincemann.springrapid.core.slicing.components;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @see ServiceComponent
 */
@Inherited
@Profile("web")
@Component
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebComponent {
}
