package io.cloudflight.platform.spring.logging.annotation;

import java.lang.annotation.*;

/**
 * Annotation providing a convenient way to add the value of a method param to the log MDC information.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(LogParams.class)
public @interface LogParam {
    /**
     * <p>Defines the field of the annotated parameter that should be printed.</p>
     * <p>If no field is defined the parameter value is printed as is.</p>
     * <p>Example:</p>
     * <ul>
     *     <li><code>field</code></li>
     *     <li><code>field.subfield</code></li>
     * </ul>
     *
     * @return path to field
     */
    String field() default "";

    /**
     * <p>Defines the name with which the value is put into the log MDC information.</p>
     * <p>If no name is defined the name used will be <code>&lt;paramName&gt;[.&lt;field&gt;]</code></p>
     *
     * @return name
     */
    String name() default "";
}
