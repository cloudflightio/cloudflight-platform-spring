package io.cloudflight.platform.spring.tracing.autoconfigure

import io.cloudflight.platform.spring.tracing.filter.RequestLoggingDataProvider
import io.cloudflight.platform.spring.tracing.filter.RequestLoggingFilter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

@AutoConfiguration
class PlatformTracingAutoConfiguration {

    @Bean
    fun requestLoggingFilter(applicationContext: ApplicationContext): FilterRegistrationBean<RequestLoggingFilter> {
        val registrationBean: FilterRegistrationBean<RequestLoggingFilter> = FilterRegistrationBean()
        registrationBean.filter =
            RequestLoggingFilter(applicationContext.getBeansOfType(RequestLoggingDataProvider::class.java).values)
        registrationBean.order = SecurityProperties.DEFAULT_FILTER_ORDER + 1
        registrationBean.addUrlPatterns(
            "/*"
        )
        return registrationBean
    }
}
