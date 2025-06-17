package com.latch;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

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
class LoggingEventCloner {
    private static LoggerContext loggerContext;

    public static LoggingEvent clone(ILoggingEvent event, String message, Map<String, String> mdcValueMap) {
        LoggingEvent newEvent = new LoggingEvent();

        newEvent.setLevel(event.getLevel());
        newEvent.setLoggerName(event.getLoggerName());
        newEvent.setTimeStamp(event.getTimeStamp());
        newEvent.setLoggerContextRemoteView(event.getLoggerContextVO());
        newEvent.setLoggerContext(getLoggerContext());
        newEvent.setThreadName(event.getThreadName());
        newEvent.setMessage(message);
        newEvent.setMDCPropertyMap(mdcValueMap);

        List<Marker> eventMarkers = event.getMarkerList();
        if (eventMarkers != null && !eventMarkers.isEmpty()) {
            eventMarkers.forEach(newEvent::addMarker);
        }

        if (event.hasCallerData()) {
            newEvent.setCallerData(event.getCallerData());
        }

        return newEvent;
    }

    /**
     * We can't set the logger context directly because that would cause issues if the logger context is not initialized yet.
     */
    private static LoggerContext getLoggerContext() {
        if (loggerContext == null) {
            loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        }

        return loggerContext;
    }
}
