package io.cloudflight.platform.spring.test.jgiven

import com.tngtech.jgiven.annotation.IsTag

/**
 * Use this annotation on a JUnit5 `@Test` method in order to link the test with one or more tickets in your
 * tracking system.
 *
 * Set the System.property `jgiven.issue.server.url` to exactly an URL like `https://<your-jira-server-domain>/browse/%s`.
 */
@IsTag(descriptionGenerator = TicketDescriptionGenerator::class)
annotation class Ticket(

    /**
     * One ore more ticket IDs, i.e. `CLFP-1`
     */
    val value: Array<String>
)
