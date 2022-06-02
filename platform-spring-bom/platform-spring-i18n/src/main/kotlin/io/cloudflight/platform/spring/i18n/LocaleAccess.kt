package io.cloudflight.platform.spring.i18n

import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

interface LocaleAccess {
    val currentLocale: Locale
        get() = LocaleContextHolder.getLocale()
}
