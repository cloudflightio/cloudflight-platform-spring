package io.cloudflight.platform.spring.i18n.autoconfigure

import io.cloudflight.platform.spring.i18n.I18nService
import io.cloudflight.platform.spring.i18n.ListResourceBundleMessageSource
import io.cloudflight.platform.spring.i18n.controller.I18nController
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean

@AutoConfiguration
@ConditionalOnProperty(prefix = "cloudflight.i18n.httpendpoint", name = ["enabled"], matchIfMissing = true)
class PlatformI18nWebAutoConfiguration {

    @Bean
    fun i18nController(
        messageSource: ListResourceBundleMessageSource,
        i18nService: I18nService
    ): I18nController {
        return I18nController(messageSource, i18nService)
    }

}