package io.cloudflight.platform.spring.logging.annotation;

import java.lang.annotation.*;

/**
 * Container annotation that aggregates several {@link LogParam} annotations.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogParams {
    LogParam[] value();
}
