package io.cloudflight.platform.spring.monitoring.autoconfigure

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.Profiles
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@ConditionalOnClass(WebSecurityConfigurerAdapter::class)
class ManagementSecurityAutoConfiguration {

    /**
     * Deactivates Spring Security for all management endpoints
     */
    @Order(Ordered.LOWEST_PRECEDENCE)
    @Configuration(proxyBeanMethods = false)
    class ActuatorSecurity : WebSecurityConfigurerAdapter() {

        override fun configure(web: WebSecurity) {
            web.ignoring().requestMatchers(EndpointRequest.toAnyEndpoint())
        }
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
