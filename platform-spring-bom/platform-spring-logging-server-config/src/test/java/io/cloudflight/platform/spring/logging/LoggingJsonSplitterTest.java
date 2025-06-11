package io.cloudflight.platform.spring.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.BasicMarkerFactory;

public class LoggingJsonSplitterTest {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingJsonSplitterTest.class);
    private final String originalLogbackFile = System.getProperty("logback.configurationFile");
    private static final org.slf4j.Marker TEST_MARKER = new BasicMarkerFactory().getMarker("TEST_MARKER");

    @BeforeEach
    void setUp() {
        System.setProperty("logback.configurationFile", "src/test/resources/logback-test-json-splitter.xml");
        configureLogback();
    }

    @AfterEach
    void tearDown() {
        if (originalLogbackFile != null) {
            System.setProperty("logback.configurationFile", originalLogbackFile);
        } else {
            System.clearProperty("logback.configurationFile");
        }
        configureLogback();
    }

    private void configureLogback() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        try {
            new ContextInitializer(loggerContext).autoConfig();
        } catch (JoranException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void logWithCloudbackConfig() {
        LOG.info("Hello World");
    }

    @Test
    void logLongMessageWithJsonSplitterFails() {
        Assertions.assertThrows(NoSuchMethodError.class, () -> LOG.info(TEST_MARKER, "This is a long message. ".repeat(1000)));
    }
}
