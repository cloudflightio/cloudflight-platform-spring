package io.cloudflight.platform.spring.monitoring.autoconfigure

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.Profiles
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.invoke
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
    fun managementEndpointFilter(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(EndpointRequest.toAnyEndpoint())

            csrf {
                disable()
            }

            cors {
                disable()
            }

            authorizeHttpRequests {
                authorize(EndpointRequest.toAnyEndpoint(), permitAll)
            }
        }
        return http.build()
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
            (environment.getProperty(SERVER_PORT_NAME) ?: SPRING_DEFAULT_PORT).let { serverPort ->
                if (environment.getProperty(MANAGEMENT_SERVER_PORT_NAME).isNullOrBlank()) {
                    // only change unless already set
                    environment.propertySources.addFirst(
                        MapPropertySource(
                            "cloudflight-management",
                            mapOf(MANAGEMENT_SERVER_PORT_NAME to serverPort.toInt() + 10000)
                        )
                    )
                }
            }
        }
    }

    companion object {
        const val SPRING_DEFAULT_PORT = "8080"

        const val SERVER_PORT_NAME = "server.port"
        const val MANAGEMENT_SERVER_PORT_NAME = "management.server.port"
    }
}
