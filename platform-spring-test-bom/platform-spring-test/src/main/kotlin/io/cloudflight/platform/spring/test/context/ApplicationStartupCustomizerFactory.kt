package io.cloudflight.platform.spring.test.context

import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfigurationAttributes
import org.springframework.test.context.ContextCustomizer
import org.springframework.test.context.ContextCustomizerFactory
import org.springframework.test.context.MergedContextConfiguration

/**
 * Thie [ContextCustomizerFactory] adds a [BufferingApplicationStartup] to the test context in order
 * to be able to tracker context startup in [SpringBootTest]s.
 */
class ApplicationStartupCustomizerFactory : ContextCustomizerFactory {
    override fun createContextCustomizer(
        testClass: Class<*>,
        configAttributes: MutableList<ContextConfigurationAttributes>
    ): ContextCustomizer {
       return TestContextCustomizer
    }

    private object TestContextCustomizer : ContextCustomizer {
        override fun customizeContext(
            context: ConfigurableApplicationContext,
            mergedConfig: MergedContextConfiguration
        ) {
            context.applicationStartup = BufferingApplicationStartup(2048)
        }

        override fun equals(other: Any?): Boolean {
            return other is TestContextCustomizer
        }

        override fun hashCode(): Int {
            return 1
        }
    }
}