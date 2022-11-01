package io.cloudflight.platform.spring.monitoring.autoconfigure

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.Profiles
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.SecurityFilterChain

@AutoConfiguration
@ConditionalOnClass(WebSecurity::class)
class ManagementSecurityAutoConfiguration {

    /**
     * Deactivates Spring Security for all management endpoints. The order of this [SecurityFilterChain]
     * is set to [ORDER], and that should come before your individual [SecurityFilterChain] implementations,
     * so your order should be higher.
     */
    @Bean
    @Order(ORDER)
    @ConditionalOnProperty(
        value = ["cloudflight.spring.security.use-deprecated-websecurity-configurer"],
        havingValue = "false",
        matchIfMissing = true
    )
    fun managementEndpointFilter(http: HttpSecurity): SecurityFilterChain {
        return http.requestMatcher(EndpointRequest.toAnyEndpoint())
            .csrf().disable()
            .cors().disable()
            .authorizeRequests().anyRequest().permitAll()
            .and()
            .build()
    }

    /**
     * In case the target application still uses the deprecated [org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter]
     * then this bean can replace the [managementEndpointFilter] by using a [WebSecurityCustomizer] calling the deprecated [WebSecurity.ignoring] method there.
     */
    @Bean
    @ConditionalOnProperty(
        value = ["cloudflight.spring.security.use-deprecated-websecurity-configurer"],
        havingValue = "true"
    )
    @Deprecated("migrate your SpringSecurity code and use SecurityFilterChain")
    fun legacyManagementEndpointFilter(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web -> web.ignoring().requestMatchers(EndpointRequest.toAnyEndpoint()) }
    }

    companion object {
        const val ORDER = 50
    }
}

/**
 * Sets the port of the management endpoints to [server.port] - 1000
 */
@Order(Ordered.LOWEST_PRECEDENCE)
class ManagementServerPortEnvironmentPostProcessor : EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        if (!environment.acceptsProfiles(
                Profiles.of(
                    ApplicationContextProfiles.TEST,
                    ApplicationContextProfiles.TEST_CONTAINER
                )
            )
        ) {
            // we do not change the port for the management services in test cases and container tests to not get any conflicts
            // with concurrent builds
            (environment.getProperty("server.port") ?: SPRING_DEFAULT_PORT).let { serverPort ->
                environment.propertySources.addFirst(
                    MapPropertySource(
                        "cloudflight-management",
                        mapOf("management.server.port" to serverPort.toInt() + 10000)
                    )
                )
            }
        }
    }

    companion object {
        const val SPRING_DEFAULT_PORT = "8080"
    }
}
