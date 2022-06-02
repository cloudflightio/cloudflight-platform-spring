package io.cloudflight.platform.spring.i18n

import java.util.*

interface I18nService {

    val availableLocales: List<Locale>
    val primaryLocale: Locale?
}