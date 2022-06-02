package io.cloudflight.platform.spring.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingConfigTest {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingConfigTest.class);

    @Test
    void logWithCloudbackConfig() {
        LOG.info("Hello World");
    }
}
