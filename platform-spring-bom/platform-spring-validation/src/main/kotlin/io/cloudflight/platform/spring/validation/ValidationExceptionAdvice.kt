package io.cloudflight.platform.spring.validation

import io.cloudflight.platform.spring.validation.api.dto.ErrorResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice(annotations = [RestController::class])
@Order(ValidationConstants.VALIDATION_ADVICE_ORDER)
class ValidationExceptionAdvice(
    private val errorResponseMapper: ErrorResponseMapper
) {

    @ExceptionHandler(BindException::class, ValidationException::class)
    fun bindFailed(exception: BindingResult): ResponseEntity<ErrorResponse> {
        return mapBindingResults(exception)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun bindFailed(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return mapBindingResults(exception.bindingResult)
    }

    private fun mapBindingResults(binding: BindingResult): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponseMapper.mapBindingResult(binding))
    }
}
