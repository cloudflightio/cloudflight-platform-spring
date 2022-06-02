package io.cloudflight.platform.spring.i18n

import org.springframework.context.MessageSource
import java.util.*

interface ListResourceBundleMessageSource : MessageSource {

    /**
     * Returns a map of key/value pairs of all configured message properties
     * (across all configured resource bundles).
     * Be aware that if 2 bundles specify the same key, the value of the first bundle
     * specifying a value for it, will be used, all later values will be ignored silently.
     *
     * @param locale current locale
     * @return key value pars
     */
    fun getAllMessages(locale: Locale): Map<String, String>
}
