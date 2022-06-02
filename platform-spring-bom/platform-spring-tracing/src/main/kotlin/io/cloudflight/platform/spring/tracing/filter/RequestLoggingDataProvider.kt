package io.cloudflight.platform.spring.tracing.filter

import org.slf4j.MDC
import javax.servlet.http.HttpServletRequest

/**
 * Implement this interface in order to add data to the [MDC] for every request by means of the [RequestLoggingFilter]
 */
interface RequestLoggingDataProvider {

    fun provideData(request: HttpServletRequest): Map<String, String>
}
