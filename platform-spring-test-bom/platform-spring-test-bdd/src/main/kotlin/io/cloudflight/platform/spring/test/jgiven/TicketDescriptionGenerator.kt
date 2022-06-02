package io.cloudflight.platform.spring.test.jgiven

import com.tngtech.jgiven.annotation.TagDescriptionGenerator
import com.tngtech.jgiven.config.TagConfiguration
import java.util.*

internal class TicketDescriptionGenerator : TagDescriptionGenerator {

    private val serverUrl = System.getProperty(TICKET_SERVER_URL, CLOUDFLIGHT_SERVER_URL)

    override fun generateDescription(
        tagConfiguration: TagConfiguration?,
        annotation: Annotation?,
        value: Any?
    ): String {
        return String.format(Locale.ENGLISH, "<a href='$serverUrl'>$serverUrl</a>", value, value);
    }

    companion object {
        const val CLOUDFLIGHT_SERVER_URL = "https://jira.cloudflight.io/browse/%s"
        const val TICKET_SERVER_URL = "jgiven.issue.server.url"
    }
}
