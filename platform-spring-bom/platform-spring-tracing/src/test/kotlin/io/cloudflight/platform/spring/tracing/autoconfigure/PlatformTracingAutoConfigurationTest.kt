package io.cloudflight.platform.spring.tracing.autoconfigure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.FilteredClassLoader
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.web.servlet.HandlerMapping

class PlatformTracingAutoConfigurationTest {

    private val configuration = AutoConfigurations.of(PlatformTracingAutoConfiguration::class.java)

    @Test
    fun registersFilterInServletApplication() {
        WebApplicationContextRunner().withConfiguration(configuration)
            .run { context ->
                assertThat(context).hasSingleBean(FilterRegistrationBean::class.java)
            }
    }

    @Test
    fun backsOffInNonWebApplication() {
        ApplicationContextRunner().withConfiguration(configuration)
            .run { context ->
                assertThat(context).doesNotHaveBean(FilterRegistrationBean::class.java)
            }
    }

    @Test
    fun backsOffWithoutSpringWebMvc() {
        WebApplicationContextRunner().withConfiguration(configuration)
            .withClassLoader(FilteredClassLoader(HandlerMapping::class.java))
            .run { context ->
                assertThat(context).doesNotHaveBean(FilterRegistrationBean::class.java)
            }
    }
}
