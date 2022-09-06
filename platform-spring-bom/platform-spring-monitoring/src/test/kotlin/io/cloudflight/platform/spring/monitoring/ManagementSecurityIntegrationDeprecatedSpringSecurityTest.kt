package io.cloudflight.platform.spring.monitoring

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("deprecated")
@SpringBootTest(classes = [ManagementSecurityIntegrationDeprecatedSpringSecurityTest.TestApplication::class])
class ManagementSecurityIntegrationDeprecatedSpringSecurityTest {

    @Test
    fun contestStarts() {
    }

    @SpringBootApplication
    class TestApplication {

        @Configuration
        @EnableWebSecurity
        class SecurityConfiguration : WebSecurityConfigurerAdapter() {

        }
    }
}