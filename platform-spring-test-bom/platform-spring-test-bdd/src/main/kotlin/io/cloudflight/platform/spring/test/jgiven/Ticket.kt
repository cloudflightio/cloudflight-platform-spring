package io.cloudflight.platform.spring.test.jgiven

import com.tngtech.jgiven.annotation.IsTag

/**
 * Use this annotation on a JUnit5 `@Test` method in order to link the test with one or more tickets in your
 * tracking system.
 *
 * Per default, the server url `https://jira.cloudflight.io/browse/%s` is being used. If your tickets come from
 * another server, set the System.property `jgiven.issue.server.url` to exactly that url.
 */
@IsTag(descriptionGenerator = TicketDescriptionGenerator::class)
annotation class Ticket(

    /**
     * One ore more ticket IDs, i.e. `CLFP-1`
     */
    val value: Array<String>
)
