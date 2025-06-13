package com.latch;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class LengthSplittingAppender extends SplittingAppenderBase<ILoggingEvent> {
    private final LoggingEventCloner loggingEventCloner;

    public LengthSplittingAppender() {
        super();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.loggingEventCloner = new LoggingEventCloner(loggerContext);
    }

    private int maxLength;
    private String sequenceKey;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getSequenceKey() {
        return sequenceKey;
    }

    public void setSequenceKey(String sequenceKey) {
        this.sequenceKey = sequenceKey;
    }

    @Override
    public boolean shouldSplit(ILoggingEvent event) {
        return event.getFormattedMessage().length() > maxLength;
    }

    @Override
    public List<ILoggingEvent> split(ILoggingEvent event) {
        List<String> logMessages = splitString(event.getFormattedMessage(), getMaxLength());

        List<ILoggingEvent> splitLogEvents = new ArrayList<>(logMessages.size());
        for (int i = 0; i < logMessages.size(); i++) {
            String message = logMessages.get(i);
            Map<String, String> seqMDCPropertyMap = new HashMap<>(event.getMDCPropertyMap());
            seqMDCPropertyMap.put(getSequenceKey(), Integer.toString(i));

            LoggingEvent clonedEvent = loggingEventCloner.clone(event, message, seqMDCPropertyMap);

            splitLogEvents.add(clonedEvent);
        }

        return splitLogEvents;
    }

    private List<String> splitString(String str, int chunkSize) {
        int fullChunks = str.length() / chunkSize;
        int remainder = str.length() % chunkSize;

        List<String> results = new ArrayList<>(remainder == 0 ? fullChunks : fullChunks + 1);
        for (int i = 0; i < fullChunks; i++) {
            results.add(str.substring(i * chunkSize, i * chunkSize + chunkSize));
        }
        if (remainder != 0) {
            results.add(str.substring(str.length() - remainder));
        }
        return results;
    }
}
