package io.cloudflight.platform.spring.validation.api.dto

interface Message {
    val message: String
    val severity: MessageSeverity
}
