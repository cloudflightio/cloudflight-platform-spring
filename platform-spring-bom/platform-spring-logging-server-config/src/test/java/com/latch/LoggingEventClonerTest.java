package com.latch;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

import java.util.Collections;
import java.util.Map;

public class LoggingEventClonerTest {
    private final LoggerContext loggerContext;
    private final LoggingEventCloner loggingEventCloner;

    public LoggingEventClonerTest() {
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.loggingEventCloner = new LoggingEventCloner(loggerContext);
    }



    @Test
    public void correctlyClonesBasicEventProperties() {
        LoggingEvent event = createLoggingEventWithContext();
        event.setLevel(Level.DEBUG);
        event.setLoggerName("loggerName");
        event.setThreadName("testThread");
        event.setTimeStamp(System.currentTimeMillis());

        LoggingEvent clonedEvent = loggingEventCloner.clone(event, "", Collections.emptyMap());

        Assertions.assertNotNull(clonedEvent);
        Assertions.assertEquals(event.getLevel(), clonedEvent.getLevel());
        Assertions.assertEquals(event.getLoggerName(), clonedEvent.getLoggerName());
        Assertions.assertEquals(event.getThreadName(), clonedEvent.getThreadName());
        Assertions.assertEquals(event.getTimeStamp(), clonedEvent.getTimeStamp());
    }

    @Test
    public void correctlyClonesMessage() {
        LoggingEvent event = createLoggingEventWithContext();
        String message = "Test message";

        LoggingEvent clonedEvent = loggingEventCloner.clone(event, message, Collections.emptyMap());

        Assertions.assertNotNull(clonedEvent);
        Assertions.assertEquals(message, clonedEvent.getMessage());
    }

    @Test
    public void correctlyClonesMDCProperties() {
        LoggingEvent event = createLoggingEventWithContext();
        Map<String, String> mdcProperties = Map.of("key1", "value1", "key2", "value2");

        LoggingEvent clonedEvent = loggingEventCloner.clone(event, "", mdcProperties);

        Assertions.assertNotNull(clonedEvent);
        Map<String, String> clonedMDCProperties = clonedEvent.getMDCPropertyMap();
        Assertions.assertEquals(2, clonedMDCProperties.size());
        Assertions.assertEquals("value1", clonedMDCProperties.get("key1"));
        Assertions.assertEquals("value2", clonedMDCProperties.get("key2"));
    }

    @Test
    public void correctlyClonesMarker() {
        LoggingEvent event = createLoggingEventWithContext();
        Marker marker = new BasicMarkerFactory().getMarker("TestMarker");
        event.addMarker(marker);

        LoggingEvent clonedEvent = loggingEventCloner.clone(event, "", Collections.emptyMap());

        Assertions.assertNotNull(clonedEvent);
        Assertions.assertEquals(marker.getName(), clonedEvent.getMarkerList().get(0).getName());
    }

    @Test
    public void correctlyClonesCallerData() {
        LoggingEvent event = createLoggingEventWithContext();
        StackTraceElement[] callerData = new StackTraceElement[] {
            new StackTraceElement("com.example.Class", "method", "Class.java", 42)
        };
        event.setCallerData(callerData);

        LoggingEvent clonedEvent = loggingEventCloner.clone(event, "", Collections.emptyMap());

        Assertions.assertTrue(clonedEvent.hasCallerData());
        StackTraceElement[] clonedCallerData = clonedEvent.getCallerData();
        Assertions.assertEquals(1, clonedCallerData.length);
        Assertions.assertEquals(callerData[0].getClassLoaderName(), clonedCallerData[0].getClassLoaderName());
    }

    private LoggingEvent createLoggingEventWithContext() {
        LoggingEvent event = new LoggingEvent();
        event.setLoggerContext(loggerContext);
        return event;
    }
}
