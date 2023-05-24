package io.cloudflight.platform.spring.monitoring.autoconfigure

import io.cloudflight.platform.spring.monitoring.autoconfigure.ManagementServerPortEnvironmentPostProcessor.Companion.MANAGEMENT_SERVER_PORT_NAME
import io.cloudflight.platform.spring.monitoring.autoconfigure.ManagementServerPortEnvironmentPostProcessor.Companion.SERVER_PORT_NAME
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.Profiles

class ManagementServerPortEnvironmentPostProcessorTest {

    @Test
    fun noExplicitManagementServerPortOverride() {
        val env = mockk<ConfigurableEnvironment>()

        every { env.acceptsProfiles(any() as Profiles) }.returns(false)
        every { env.getProperty(SERVER_PORT_NAME) }.returns("1024")
        every { env.getProperty(MANAGEMENT_SERVER_PORT_NAME) }.returns("1025")
        val propertySources=mockk<MutablePropertySources>()
        every { env.propertySources }.returns(propertySources)

        ManagementServerPortEnvironmentPostProcessor().postProcessEnvironment(env, SpringApplication())

        verify (exactly = 0){ propertySources.addFirst(any()) }
    }

    @Test
    fun deriveManagementServerPortFromServerPort() {
        val env = mockk<ConfigurableEnvironment>(relaxed = true)

        every { env.acceptsProfiles(any() as Profiles) }.returns(false)
        every { env.getProperty(SERVER_PORT_NAME) }.returns("1024")
        every { env.getProperty(MANAGEMENT_SERVER_PORT_NAME) }.returns(null)
        val propertySources=mockk<MutablePropertySources>()
        every { env.propertySources }.returns(propertySources)
        justRun { propertySources.addFirst(any()) }

        ManagementServerPortEnvironmentPostProcessor().postProcessEnvironment(env, SpringApplication())

        verify (exactly = 1){ propertySources.addFirst(any()) }
    }
}