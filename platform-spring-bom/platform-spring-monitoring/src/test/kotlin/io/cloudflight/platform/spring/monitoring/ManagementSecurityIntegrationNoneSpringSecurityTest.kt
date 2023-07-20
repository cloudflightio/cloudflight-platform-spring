package io.cloudflight.platform.spring.monitoring

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("none")
@SpringBootTest(classes = [ManagementSecurityIntegrationNoneSpringSecurityTest.TestApplication::class])
class ManagementSecurityIntegrationNoneSpringSecurityTest {

    @Test
    fun contestStarts() {
    }

    @SpringBootApplication
    class TestApplication {

        @Configuration
        @EnableWebSecurity
        class SecurityConfiguration {

            @Bean
            fun customerFilter(http: HttpSecurity): DefaultSecurityFilterChain? {
                http {
                    csrf {
                        disable()
                    }
                }
                return http.build()
            }
        }
    }
}