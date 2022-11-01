package io.cloudflight.platform.spring.i18n.autoconfigure

import io.cloudflight.platform.spring.i18n.I18nService
import io.cloudflight.platform.spring.i18n.impl.I18nServiceImpl
import io.cloudflight.platform.spring.i18n.impl.PlatformMessageSourceImpl
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.autoconfigure.context.MessageSourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.util.StringUtils

@AutoConfiguration(before = [MessageSourceAutoConfiguration::class])
@EnableConfigurationProperties(value = [I18nProperties::class])
class PlatformI18nAutoConfiguration : MessageSourceAutoConfiguration() {

    @Bean
    override fun messageSource(properties: MessageSourceProperties): MessageSource {
        val messageSource = PlatformMessageSourceImpl()
        if (StringUtils.hasText(properties.basename)) {
            messageSource.setBasenames(
                *StringUtils
                    .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.basename))
            )
        }
        if (properties.encoding != null) {
            messageSource.setDefaultEncoding(properties.encoding.name())
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale)
        val cacheDuration = properties.cacheDuration
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis())
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat)
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage)

        return messageSource
    }

    @Bean("platformI18nService")
    fun i18nService(i18nProperties: I18nProperties): I18nService {
        return I18nServiceImpl(i18nProperties)
    }
}
