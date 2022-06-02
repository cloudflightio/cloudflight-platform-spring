package io.cloudflight.platform.spring.i18n.impl

import io.cloudflight.platform.spring.i18n.I18nService
import io.cloudflight.platform.spring.i18n.autoconfigure.I18nProperties
import java.util.*

internal class I18nServiceImpl(private val properties: I18nProperties) : I18nService {

    init {
        require(properties.locales.isNotEmpty()) {
            "at least one locale must be available, configured by 'cloudflight.i18n.locales'"
        }
        require(properties.primary == null || properties.locales.contains(properties.primary!!)) {
            "${properties.primary} is not part of 'cloudflight.i18n.locales': ${properties.locales}"
        }
    }

    override val availableLocales: List<Locale>
        get() = properties.locales
    override val primaryLocale: Locale?
        get() = properties.primary
}
