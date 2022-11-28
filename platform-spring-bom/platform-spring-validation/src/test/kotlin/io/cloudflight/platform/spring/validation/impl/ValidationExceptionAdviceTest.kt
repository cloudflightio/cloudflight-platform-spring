package io.cloudflight.platform.spring.validation.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.platform.spring.validation.ValidationException
import io.cloudflight.platform.spring.validation.api.dto.ErrorResponse
import io.cloudflight.platform.spring.validation.api.dto.FieldMessageDto
import io.cloudflight.platform.spring.validation.api.dto.GlobalMessageDto
import io.cloudflight.platform.spring.validation.api.dto.MessageSeverity
import io.cloudflight.platform.spring.validation.autoconfigure.PlatformValidationAutoConfiguration
import io.cloudflight.platform.spring.validation.impl.ValidationExceptionAdviceTest.ValidationExceptionAdviceController.Companion.mustNotBeBlank
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@ContextConfiguration(
    classes = [
        PlatformValidationAutoConfiguration::class,
        ValidationExceptionAdviceTest.ValidationExceptionAdviceController::class
    ]
)
@WebMvcTest
class ValidationExceptionAdviceTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `handle bind exception`() {
        val payload = ValidationExceptionAdviceController.ValidationPayload("", null)
        val expectedError = ErrorResponse(
            fieldMessages = listOf(
                FieldMessageDto(
                    ValidationExceptionAdviceController.ValidationPayload::nonBlankString.name,
                    GlobalMessageDto(mustNotBeBlank, MessageSeverity.ERROR)
                )
            )
        )

        mockMvc.post("/api/test/bind", payload)
            .andExpect {
                status { isBadRequest() }
                content { json(objectMapper.writeValueAsString(expectedError)) }
            }
    }

    @Test
    fun `handle validation exception`() {
        val payload = ValidationExceptionAdviceController.ValidationPayload("nonBlank", "321")
        val expectedError = ErrorResponse(
            fieldMessages = listOf(
                FieldMessageDto(
                    ValidationExceptionAdviceController.ValidationPayload::mustMatchOneTwoThree.name,
                    GlobalMessageDto(ValidationExceptionAdviceController.mustMatchErrorCode, MessageSeverity.ERROR)
                )
            )
        )

        mockMvc.post("/api/test/validation", payload)
            .andExpect {
                status { isBadRequest() }
                content { json(objectMapper.writeValueAsString(expectedError)) }
            }
    }

    @RestController
    internal class ValidationExceptionAdviceController {

        companion object {
            const val mustNotBeBlank = "must.not.blank"
            const val mustMatchErrorCode = "must.match.123"
        }

        class ValidationPayload(
            @field:NotBlank(message = mustNotBeBlank) var nonBlankString: String?,
            var mustMatchOneTwoThree: String?
        )

        @PostMapping("/api/test/bind")
        fun forBindException(@Valid payload: ValidationPayload) = payload.nonBlankString

        @PostMapping("/api/test/validation")
        fun forValidationException(payload: ValidationPayload) {
            if (payload.mustMatchOneTwoThree != "123") {
                throw ValidationException(payload, "payload").apply {
                    rejectValue(ValidationPayload::mustMatchOneTwoThree.name, mustMatchErrorCode, mustMatchErrorCode)
                }
            }
        }

    }
}
