package io.cloudflight.platform.spring.i18n

import mu.KotlinLogging
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException
import java.util.*

private val LOG = KotlinLogging.logger { }

fun MessageSource.safeGetMessage(code: String, locale: Locale): String {
    return this.safeGetMessage(MessageSourceResolvable { arrayOf(code) }, locale)
}

fun MessageSource.safeGetMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
    try {
        return this.getMessage(resolvable, locale)
    } catch (e: NoSuchMessageException) {
        LOG.error(e) { e.message }
        return e.message ?: "No message found for code '${resolvable.codes?.last()}'"
    }
}
