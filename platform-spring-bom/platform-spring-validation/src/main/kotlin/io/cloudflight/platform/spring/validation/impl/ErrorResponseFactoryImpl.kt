package io.cloudflight.platform.spring.validation.impl

import io.cloudflight.platform.spring.i18n.LocaleAccess
import io.cloudflight.platform.spring.i18n.safeGetMessage
import io.cloudflight.platform.spring.validation.ErrorResponseFactory
import io.cloudflight.platform.spring.validation.api.dto.ErrorResponse
import io.cloudflight.platform.spring.validation.api.dto.FieldMessageDto
import io.cloudflight.platform.spring.validation.api.dto.GlobalMessageDto
import io.cloudflight.platform.spring.validation.api.dto.MessageSeverity
import org.springframework.context.MessageSource

internal class ErrorResponseFactoryImpl(private val messageSource: MessageSource) : ErrorResponseFactory, LocaleAccess {

    override fun createFieldMessageResponse(field: String, code: String): ErrorResponse {
        val error = FieldMessageDto(
            field,
            GlobalMessageDto(messageSource.safeGetMessage(code, currentLocale), MessageSeverity.ERROR)
        )
        return ErrorResponse(fieldMessages = listOf(error))
    }

    override fun createGlobalMessageResponse(code: String): ErrorResponse {
        val error = GlobalMessageDto(messageSource.safeGetMessage(code, currentLocale), MessageSeverity.ERROR)
        return ErrorResponse(globalMessages = listOf(error))
    }
}