package io.cloudflight.platform.spring.logging.autoconfigure;

import io.cloudflight.platform.spring.logging.interceptor.LogParamInterceptor;
import io.cloudflight.platform.spring.logging.service.LogFlusher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Logging Service
 * <p>
 * It also ensures that all logs are sent when the application shuts down.
 *
 * @author Clemens Grabmann
 */
@AutoConfiguration
public class LoggingServiceAutoConfiguration {

    @Bean
    public LogFlusher logFlusher() {
        return new LogFlusher();
    }

    @Bean
    public LogParamInterceptor logParamInterceptor() {
        return new LogParamInterceptor();
    }

}
