package io.cloudflight.platform.spring.i18n

data class LocaleInformation(
        val activeLocale: String,
        val availableLocales: List<String>
)
