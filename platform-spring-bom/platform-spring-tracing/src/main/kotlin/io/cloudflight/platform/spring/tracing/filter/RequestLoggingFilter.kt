package io.cloudflight.platform.spring.tracing.filter

import io.cloudflight.platform.spring.logging.mdc.mdcScope
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerMapping
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestLoggingFilter(private val dataProvider: Collection<RequestLoggingDataProvider>) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) = mdcScope {
        val now = System.currentTimeMillis()
        val originalRequestUri: String = request.requestURI.toString()
        val requestIp = "${request.remoteAddr} / ${request.remoteHost}"

        MDC.put("requestUri", originalRequestUri)

        val originalQueryString: String? = request.queryString
        if (originalQueryString != null) {
            MDC.put("queryString", originalQueryString)
        }
        val requestPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)
        if (requestPattern != null) {
            MDC.put("requestPattern", requestPattern.toString())
        }
        MDC.put("method", request.method)
        MDC.put("requestedFrom", requestIp)

        dataProvider.forEach {
            val data = it.provideData(request)
            data.forEach { d ->
                MDC.put(d.key, d.value)
            }
        }

        try {
            filterChain.doFilter(request, response)
        } catch (e: HttpRequestMethodNotSupportedException) {
            LOG.warn(
                "HttpRequestMethodNotSupportedException in CurrentUserLoggingFilter with url $originalRequestUri:",
                e
            )
        } finally {
            if (LOG.isTraceEnabled) {
                with(request) {
                    val requestHeaders = request.headerNames.toList().map {
                        "$it:${request.getHeader(it)}"
                    }.joinToString(";")

                    val duration = System.currentTimeMillis() - now
                    LOG.trace(
                        "Request $method $originalRequestUri took $duration ms.",
                        kv("status", response.status),
                        kv("duration", duration),
                        kv("requestHeaders", requestHeaders)
                    )
                }
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger("RequestLogger")
    }
}
