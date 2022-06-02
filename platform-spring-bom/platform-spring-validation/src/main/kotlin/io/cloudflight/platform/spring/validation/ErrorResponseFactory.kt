package io.cloudflight.platform.spring.validation

import io.cloudflight.platform.spring.validation.api.dto.ErrorResponse

interface ErrorResponseFactory {

    fun createFieldMessageResponse(field: String, code: String): ErrorResponse

    fun createGlobalMessageResponse(code: String): ErrorResponse
}