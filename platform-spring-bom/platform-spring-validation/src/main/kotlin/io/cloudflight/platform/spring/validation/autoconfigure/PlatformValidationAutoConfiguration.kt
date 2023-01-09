package io.cloudflight.platform.spring.validation.autoconfigure

import io.cloudflight.platform.spring.validation.ErrorResponseFactory
import io.cloudflight.platform.spring.validation.ErrorResponseMapper
import io.cloudflight.platform.spring.validation.impl.ErrorResponseFactoryImpl
import io.cloudflight.platform.spring.validation.impl.ErrorResponseMapperImpl
import io.cloudflight.platform.spring.validation.ValidationExceptionAdvice
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean

@AutoConfiguration
class PlatformValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun errorResponseFactory(messageSource: MessageSource): ErrorResponseFactory {
        return ErrorResponseFactoryImpl(messageSource)
    }

    @Bean
    @ConditionalOnMissingBean
    internal fun errorResponseMapper(messageSource: MessageSource): ErrorResponseMapper {
        return ErrorResponseMapperImpl(messageSource)
    }

    @Bean
    internal fun exceptionAdvice(errorResponseMapper: ErrorResponseMapper): ValidationExceptionAdvice {
        return ValidationExceptionAdvice(errorResponseMapper)
    }
}
