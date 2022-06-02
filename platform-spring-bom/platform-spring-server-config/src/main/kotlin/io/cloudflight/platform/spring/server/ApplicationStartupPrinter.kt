package io.cloudflight.platform.spring.server

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.context.metrics.buffering.StartupTimeline
import org.springframework.context.ApplicationListener

/**
 * Internal class utilizing Spring Boot's [BufferingApplicationStartup] functionality to print
 * statistics of
 *
 * Set the logger `io.cloudflight.platform.server` to `TRACE` in your `logback-spring.xml` in order to have
 * the startup statistics in the log
 */
internal class ApplicationStartupPrinter(private val startup: BufferingApplicationStartup) :
    ApplicationListener<ApplicationStartedEvent> {

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        if (LOG.isTraceEnabled) {
            printStartup()
        }
    }

    private fun printStartup() {
        val timeline = startup.bufferedTimeline

        val buffer = StringBuffer("Server startup summary: " + System.lineSeparator())
        buffer.append("-".repeat(HEADER_WIDTH)).append(System.lineSeparator())
        buffer.append("ms".padStart(TIME_COLUMN_WIDTH))
        buffer.append("  ")
        buffer.append("Step").append(System.lineSeparator())
        buffer.append("-".repeat(HEADER_WIDTH)).append(System.lineSeparator())

        timeline.events.filter { it.startupStep.parentId == null || it.startupStep.parentId == 0.toLong() }
            .sortedBy { it.startupStep.id }.forEach {
                printEvent(buffer, it, 0, timeline)
            }

        LOG.trace(buffer.toString())
    }

    private fun printEvent(
        buffer: StringBuffer,
        event: StartupTimeline.TimelineEvent,
        indent: Int,
        timeline: StartupTimeline
    ) {
        buffer.append(event.duration.toMillis().toString().padStart(TIME_COLUMN_WIDTH))
        buffer.append(" ".repeat(2 + (indent * INDENT)))
        buffer.append(event.startupStep.name).append("(")
        buffer.append(event.startupStep.tags.joinToString(transform = { tag -> tag.key + "=" + tag.value }))
        buffer.append(")")
        buffer.append(System.lineSeparator())
        timeline.events.filter { it.startupStep.parentId == event.startupStep.id }.sortedBy { it.startupStep.id }
            .forEach {
                printEvent(buffer, it, indent + 1, timeline)
            }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ApplicationStartupPrinter::class.java)
        private const val TIME_COLUMN_WIDTH = 10
        private const val INDENT = 2
        private const val HEADER_WIDTH = 100
    }
}