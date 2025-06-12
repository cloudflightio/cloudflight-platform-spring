package com.latch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LengthSplittingAppenderTest {

    private static final int MAX_MESSAGE_LENGTH = 50;
    private static final String BASE_STRING = "0123456789";
    private static final String LOREM_PATH = "logging_message.txt";

    private final LengthSplittingAppender splitter;

    public LengthSplittingAppenderTest() {
        this.splitter = new LengthSplittingAppender();
        splitter.setMaxLength(MAX_MESSAGE_LENGTH);
        splitter.setSequenceKey("seq");
        Assertions.assertEquals(MAX_MESSAGE_LENGTH, splitter.getMaxLength());
    }

    @Test
    public void testEmpty() {
        LoggingEvent event = new LoggingEvent();
        event.setMessage("");
        Assertions.assertFalse(splitter.shouldSplit(event));
    }

    @Test
    public void testLessThanMax() {
        LoggingEvent event = new LoggingEvent();
        event.setMessage(
                String.join("", Collections.nCopies(1, BASE_STRING)));
        Assertions.assertFalse(splitter.shouldSplit(event));
    }

    @Test
    public void testEqualToMax() {
        LoggingEvent event = new LoggingEvent();
        event.setMessage(
                String.join("", Collections.nCopies(5, BASE_STRING)));
        Assertions.assertEquals(MAX_MESSAGE_LENGTH, 5 * BASE_STRING.length());
        Assertions.assertFalse(splitter.shouldSplit(event));
    }

    @Test
    public void testGreaterThanMaxAndMultipleOfMax() {
        LoggingEvent event = new LoggingEvent();
        event.setMessage(
                String.join("", Collections.nCopies(50, BASE_STRING)));
        Assertions.assertTrue(splitter.shouldSplit(event));

        List<ILoggingEvent> splitEvents = splitter.split(event);

        Assertions.assertEquals(
                event.getFormattedMessage().length() / MAX_MESSAGE_LENGTH,
                splitEvents.size());
    }

    @Test
    public void testGreaterThanMaxAndNotMultipleOfMax() {
        LoggingEvent event = new LoggingEvent();
        event.setMessage(
                String.join("", Collections.nCopies(51, BASE_STRING)));
        Assertions.assertTrue(splitter.shouldSplit(event));

        List<ILoggingEvent> splitEvents = splitter.split(event);

        Assertions.assertEquals(
                event.getFormattedMessage().length() / MAX_MESSAGE_LENGTH + 1,
                splitEvents.size());
    }

    @Test
    public void testSplitIntegrity() {
        String loremIpsum = readTextFromResource(LOREM_PATH);
        LoggingEvent event = new LoggingEvent();
        event.setMessage(loremIpsum);

        List<ILoggingEvent> splitEvents = splitter.split(event);

        Assertions.assertEquals(event.getFormattedMessage(), recreateMessage(splitEvents));
    }

    private String recreateMessage(List<ILoggingEvent> splitEvents) {
        StringBuilder sb = new StringBuilder();

        for (ILoggingEvent splitEvent : splitEvents) {
            sb.append(splitEvent.getFormattedMessage());
        }
        
        return sb.toString();
    }

    private String readTextFromResource(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines().collect(Collectors.joining(""));
    }
}
