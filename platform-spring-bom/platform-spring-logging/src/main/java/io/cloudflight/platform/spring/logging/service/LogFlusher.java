package io.cloudflight.platform.spring.logging.service;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author Thomas Reinthaler
 */
public class LogFlusher implements DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(LogFlusher.class);

    @Override
    public void destroy() {
        LOG.info("Shutdown logger context.");
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();
    }
}
