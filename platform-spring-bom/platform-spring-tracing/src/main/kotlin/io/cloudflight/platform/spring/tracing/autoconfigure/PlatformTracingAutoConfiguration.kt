package io.cloudflight.platform.spring.tracing.autoconfigure

import io.cloudflight.platform.spring.tracing.filter.RequestLoggingDataProvider
import io.cloudflight.platform.spring.tracing.filter.RequestLoggingFilter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.HandlerMapping

// spring-webmvc is only a compileOnly dependency, the application brings its own web stack
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(HandlerMapping::class)
class PlatformTracingAutoConfiguration {

    @Bean
    fun requestLoggingFilter(applicationContext: ApplicationContext): FilterRegistrationBean<RequestLoggingFilter> {
        val registrationBean: FilterRegistrationBean<RequestLoggingFilter> = FilterRegistrationBean()
        registrationBean.setFilter(
            RequestLoggingFilter(applicationContext.getBeansOfType(RequestLoggingDataProvider::class.java).values)
        )
        // DEFAULT_FILTER_ORDER is a compile-time constant and gets inlined, so the
        // spring-boot-security dependency is only needed at compile time
        registrationBean.order = SecurityFilterProperties.DEFAULT_FILTER_ORDER + 1
        registrationBean.addUrlPatterns(
            "/*"
        )
        return registrationBean
    }
}
