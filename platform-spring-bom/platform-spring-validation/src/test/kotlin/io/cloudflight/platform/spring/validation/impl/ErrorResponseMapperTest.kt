package io.cloudflight.platform.spring.validation.impl

import io.cloudflight.platform.spring.validation.api.dto.FieldMessageDto
import io.cloudflight.platform.spring.validation.api.dto.GlobalMessageDto
import io.cloudflight.platform.spring.validation.api.dto.MessageSeverity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.MapBindingResult
import java.util.*

internal class ErrorResponseMapperTest {

    val locale = Locale.ENGLISH

    val messageSource: MessageSource = mockk()

    val errorResponseMapper =
        ErrorResponseMapperImpl(messageSource)

    @BeforeEach
    fun setup() {
        LocaleContextHolder.setLocale(locale)
    }

    @Test
    fun `map binding result with field errors only`() {
        val fieldName = "myField"
        val errorCode = "core.my.error"
        val message = "My error message"
        val bindingResult = MapBindingResult(mapOf<String, String>(), "testMap")
        bindingResult.rejectValue(fieldName, errorCode)

        every {
            messageSource.getMessage(match { it.codes!!.contains(errorCode) }, locale)
        } returns message

        val result = errorResponseMapper.mapBindingResult(bindingResult)

        val expectedFieldError = FieldMessageDto(fieldName, GlobalMessageDto(message, MessageSeverity.ERROR))
        assertThat(result.globalMessages).isEmpty()
        assertThat(result.fieldMessages).hasSize(1)
                .first()
                .isEqualTo(expectedFieldError)
    }

    @Test
    fun `map binding result with global errors only`() {
        val errorCode = "core.my.error"
        val message = "My error message"
        val bindingResult = MapBindingResult(mapOf<String, String>(), "testMap")
        bindingResult.reject(errorCode)

        every {
            messageSource.getMessage(match { it.codes!!.contains(errorCode) }, locale)
        } returns message

        val result = errorResponseMapper.mapBindingResult(bindingResult)

        val expectedGlobalError = GlobalMessageDto(message, MessageSeverity.ERROR)
        assertThat(result.globalMessages).hasSize(1)
                .first()
                .isEqualTo(expectedGlobalError)
        assertThat(result.fieldMessages).isEmpty()
    }

    @Test
    fun `map binding result without errors`() {
        val bindingResult = MapBindingResult(mapOf<String, String>(), "testMap")

        val result = errorResponseMapper.mapBindingResult(bindingResult)

        assertThat(result.globalMessages).isEmpty()
        assertThat(result.fieldMessages).isEmpty()
    }

}
