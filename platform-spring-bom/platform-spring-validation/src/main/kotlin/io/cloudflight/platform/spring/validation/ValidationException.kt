package io.cloudflight.platform.spring.validation

import io.cloudflight.platform.spring.validation.impl.ValidationExceptionAdvice
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult

/**
 * Provides an equivalent of [BindException] to transport validation failure to clients via [ValidationExceptionAdvice].
 *
 * This is a safe alternative and can be used inside [org.springframework.transaction.annotation.Transactional] methods,
 * as it ensures any changes will be rolled back due to being subclassed from [RuntimeException].
 */
class ValidationException private constructor(private val bindingResult: BindingResult) : RuntimeException(),
    BindingResult by bindingResult {
    constructor(target: Any, objectName: String) : this(BeanPropertyBindingResult(target, objectName))

    override val message: String
        get() = bindingResult.toString()

    override fun equals(other: Any?): Boolean {
        return this === other || bindingResult.equals(other)
    }

    override fun hashCode(): Int {
        return bindingResult.hashCode()
    }
}
