package io.cloudflight.platform.spring.i18n.controller

import io.cloudflight.platform.spring.i18n.I18nService
import io.cloudflight.platform.spring.i18n.ListResourceBundleMessageSource
import io.cloudflight.platform.spring.i18n.LocaleInformation
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/i18n")
class I18nController(private val messageSource: ListResourceBundleMessageSource,
                     private val i18nService: I18nService) {

    @GetMapping("/{locale}")
    fun getTranslations(@PathVariable() locale: Locale): Map<String, String> {
        return messageSource.getAllMessages(locale)
    }

    @GetMapping("/localeInformation")
    fun getLocaleInformation(): LocaleInformation {
        return LocaleInformation(LocaleContextHolder.getLocale().language, i18nService.availableLocales.map { it.language })
    }

}
