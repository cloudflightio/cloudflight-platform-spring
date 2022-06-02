package io.cloudflight.platform.spring.validation.impl

import io.cloudflight.platform.spring.i18n.LocaleAccess
import io.cloudflight.platform.spring.i18n.safeGetMessage
import io.cloudflight.platform.spring.validation.ErrorResponseMapper
import io.cloudflight.platform.spring.validation.api.dto.ErrorResponse
import io.cloudflight.platform.spring.validation.api.dto.FieldMessageDto
import io.cloudflight.platform.spring.validation.api.dto.GlobalMessageDto
import io.cloudflight.platform.spring.validation.api.dto.MessageSeverity
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError

@Component
class ErrorResponseMapperImpl(private val messageSource: MessageSource) : LocaleAccess, ErrorResponseMapper {
    override fun mapBindingResult(bindingResult: BindingResult): ErrorResponse {
        val fieldMessages = bindingResult.fieldErrors
            .map { FieldMessageDto(it.field, mapMessage(it)) }
        val globalMessages = bindingResult.globalErrors
            .map(this::mapMessage)

        return ErrorResponse(fieldMessages, globalMessages)
    }

    private fun mapMessage(error: ObjectError) = GlobalMessageDto(
        message = messageSource.safeGetMessage(error, currentLocale),
        severity = MessageSeverity.ERROR
    )
}
