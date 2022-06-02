package io.cloudflight.platform.spring.validation.api.dto

data class ErrorResponse(
        val fieldMessages: List<FieldMessageDto> = emptyList(),
        val globalMessages: List<GlobalMessageDto> = emptyList()
)
