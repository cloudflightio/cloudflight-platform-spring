package io.cloudflight.platform.spring.i18n

import io.cloudflight.platform.spring.i18n.autoconfigure.PlatformI18nAutoConfiguration
import io.cloudflight.platform.spring.i18n.autoconfigure.PlatformI18nWebAutoConfiguration
import io.cloudflight.platform.spring.i18n.controller.I18nController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.test.context.runner.WebApplicationContextRunner

class PlatformI18nWebAutoConfigurationTest {

    private val configuration = AutoConfigurations.of(
        PlatformI18nAutoConfiguration::class.java,
        PlatformI18nWebAutoConfiguration::class.java
    )

    @Test
    fun registersControllerInWebApplication() {
        WebApplicationContextRunner().withConfiguration(configuration)
            .run { context ->
                assertThat(context).hasSingleBean(I18nController::class.java)
            }
    }

    @Test
    fun noControllerInNonWebApplication() {
        ApplicationContextRunner().withConfiguration(configuration)
            .run { context ->
                assertThat(context).hasSingleBean(I18nService::class.java)
                assertThat(context).doesNotHaveBean(I18nController::class.java)
            }
    }
}
