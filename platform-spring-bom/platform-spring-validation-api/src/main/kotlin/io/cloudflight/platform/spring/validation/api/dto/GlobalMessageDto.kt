package io.cloudflight.platform.spring.validation.api.dto

data class GlobalMessageDto(
        override val message: String,
        override val severity: MessageSeverity
) : Message
