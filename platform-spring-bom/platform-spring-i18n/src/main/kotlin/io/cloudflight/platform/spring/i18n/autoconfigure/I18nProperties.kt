package io.cloudflight.platform.spring.i18n.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties(prefix = "cloudflight.i18n")
class I18nProperties {
    var locales: List<Locale> = listOf(Locale.GERMAN)
    var primary: Locale? = null
}