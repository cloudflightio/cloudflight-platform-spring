package io.cloudflight.platform.spring.test.jgiven

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TicketDescriptionGeneratorTest {

    @Test
    fun useDefaultServer() {
        val descriptor = TicketDescriptionGenerator()
        val url = descriptor.generateDescription(null, null, "CLF-3")
        assertThat(url).isEqualTo("<a href='https://jira.unknown.com/browse/CLF-3'>https://jira.unknown.com/browse/CLF-3</a>")
    }
}