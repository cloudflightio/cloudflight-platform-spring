package io.cloudflight.platform.spring.i18n

import io.cloudflight.platform.spring.i18n.autoconfigure.I18nProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [I18nTestApplication::class])
@AutoConfigureMockMvc
class I18nApplicationTest(
    @Autowired private val messageSource: MessageSource,
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val i18nProperties: I18nProperties
) {

    @Test
    fun primaryLocaleInitialized() {
        assertThat(i18nProperties.primary!!.language)
            .isEqualToIgnoringCase("japanese")
    }

    @Test
    fun plainI18n() {
        assertThat(messageSource.getMessage("testMessage", null, Locale.JAPANESE))
            .isEqualTo("testMessageValue-japanese")
        assertThat(messageSource.getMessage("testMessage", null, Locale.ENGLISH))
            .isEqualTo("testMessageValue-english")
    }

    @Test
    fun i18nPlaceholders() {
        assertThat(messageSource.getMessage("testMessagePlaceholderFrontend", null, Locale.JAPANESE))
            .isEqualTo("testMessageValueWithFrontendPlaceholder: {{somePlaceholder}}")

        assertThat(messageSource.getMessage("testMessagePlaceholderBackend", arrayOf("foo"), Locale.JAPANESE))
            .isEqualTo("testMessageValueWithBackendPlaceholder: foo")
    }

    @Test
    fun httpEndpoint() {
        mockMvc.perform(get("/api/i18n/ja"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.testMessage").value("testMessageValue-japanese"))
            // both backend and frontend placeholders must go through unprocessed
            .andExpect(jsonPath("$.testMessagePlaceholderBackend").value("testMessageValueWithBackendPlaceholder: {0}"))
            .andExpect(jsonPath("$.testMessagePlaceholderFrontend").value("testMessageValueWithFrontendPlaceholder: {{somePlaceholder}}"));

        mockMvc.perform(get("/api/i18n/en"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.testMessage").value("testMessageValue-english"))
    }

}