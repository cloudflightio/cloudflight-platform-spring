package com.latch;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.Marker;

import java.util.List;

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
class LoggingEventCloner {

    static LoggingEvent clone(ILoggingEvent event, LoggerContext loggerContext) {
        LoggingEvent logEventPartition = new LoggingEvent();

        logEventPartition.setLevel(event.getLevel());
        logEventPartition.setLoggerName(event.getLoggerName());
        logEventPartition.setTimeStamp(event.getTimeStamp());
        logEventPartition.setLoggerContextRemoteView(event.getLoggerContextVO());
        logEventPartition.setLoggerContext(loggerContext);
        logEventPartition.setThreadName(event.getThreadName());

        List<Marker> eventMarkers = event.getMarkerList();
        if (eventMarkers != null && !eventMarkers.isEmpty()) {
            logEventPartition.getMarkerList().addAll(eventMarkers);
        }

        if (event.hasCallerData()) {
            logEventPartition.setCallerData(event.getCallerData());
        }

        return logEventPartition;
    }
}
