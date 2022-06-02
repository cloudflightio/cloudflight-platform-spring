package io.cloudflight.platform.spring.validation

import io.cloudflight.platform.spring.validation.api.dto.ErrorResponse
import org.springframework.validation.BindingResult

/**
 * Mapper which is used to map any [BindingResult] to an [ErrorResponse] to be transported to clients.
 *
 * Providing a bean of this type allows customizing and extending the data provided in the [ErrorResponse].
 */
interface ErrorResponseMapper {
    fun mapBindingResult(bindingResult: BindingResult): ErrorResponse
}
