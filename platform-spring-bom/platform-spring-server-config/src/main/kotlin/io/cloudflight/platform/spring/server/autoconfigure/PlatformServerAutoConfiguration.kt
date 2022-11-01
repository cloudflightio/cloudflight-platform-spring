package io.cloudflight.platform.spring.server.autoconfigure

import io.cloudflight.platform.spring.server.ApplicationStartupPrinter
import io.cloudflight.platform.spring.server.ServerModuleIdentification
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration
@ComponentScan(basePackageClasses = [ServerModuleIdentification::class])
class PlatformServerAutoConfiguration {

    @Bean
    @ConditionalOnBean(value = [BufferingApplicationStartup::class])
    internal fun startupPrinter(startup: BufferingApplicationStartup): ApplicationStartupPrinter {
        return ApplicationStartupPrinter(startup)
    }

}