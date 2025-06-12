package com.latch;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
 * MIT License
 *
 * Copyright (c) 2019 Latchable, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class LengthSplittingAppenderTest {
    private static final int MAX_MESSAGE_LENGTH = 50;
    private static final String BASE_STRING = "0123456789";
    private static final String LOREM_PATH = "logging_message.txt";

    private final LoggerContext loggerContext;
    private final LengthSplittingAppender splitter;

    public LengthSplittingAppenderTest() {
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.splitter = new LengthSplittingAppender();
        splitter.setMaxLength(MAX_MESSAGE_LENGTH);
        splitter.setSequenceKey("seq");
        Assertions.assertEquals(MAX_MESSAGE_LENGTH, splitter.getMaxLength());
    }

    @Test
    public void testEmpty() {
        LoggingEvent event = createLoggingEvent("");
        Assertions.assertFalse(splitter.shouldSplit(event));
    }

    @Test
    public void testLessThanMax() {
        LoggingEvent event = createLoggingEvent(String.join("", Collections.nCopies(1, BASE_STRING)));
        Assertions.assertFalse(splitter.shouldSplit(event));
    }

    @Test
    public void testEqualToMax() {
        LoggingEvent event = createLoggingEvent(String.join("", Collections.nCopies(5, BASE_STRING)));
        Assertions.assertEquals(MAX_MESSAGE_LENGTH, 5 * BASE_STRING.length());
        Assertions.assertFalse(splitter.shouldSplit(event));
    }

    @Test
    public void testGreaterThanMaxAndMultipleOfMax() {
        LoggingEvent event = createLoggingEvent(String.join("", Collections.nCopies(50, BASE_STRING)));
        Assertions.assertTrue(splitter.shouldSplit(event));

        List<ILoggingEvent> splitEvents = splitter.split(event);

        Assertions.assertEquals(
                event.getFormattedMessage().length() / MAX_MESSAGE_LENGTH,
                splitEvents.size());
    }

    @Test
    public void testGreaterThanMaxAndNotMultipleOfMax() {
        LoggingEvent event = createLoggingEvent(String.join("", Collections.nCopies(51, BASE_STRING)));
        Assertions.assertTrue(splitter.shouldSplit(event));

        List<ILoggingEvent> splitEvents = splitter.split(event);

        Assertions.assertEquals(
                event.getFormattedMessage().length() / MAX_MESSAGE_LENGTH + 1,
                splitEvents.size());
    }

    @Test
    public void testSplitIntegrity() {
        String loremIpsum = readTextFromResource(LOREM_PATH);
        LoggingEvent event = createLoggingEvent(loremIpsum);

        List<ILoggingEvent> splitEvents = splitter.split(event);

        Assertions.assertEquals(event.getFormattedMessage(), recreateMessage(splitEvents));
    }

    private LoggingEvent createLoggingEvent(String message) {
        LoggingEvent event = new LoggingEvent();
        event.setMessage(message);
        event.setLoggerContext(loggerContext);
        return event;
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
