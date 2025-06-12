package com.latch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.Marker;

import java.util.List;

class LoggingEventCloner {

    static LoggingEvent clone(ILoggingEvent event) {
        LoggingEvent logEventPartition = new LoggingEvent();

        logEventPartition.setLevel(event.getLevel());
        logEventPartition.setLoggerName(event.getLoggerName());
        logEventPartition.setTimeStamp(event.getTimeStamp());
        logEventPartition.setLoggerContextRemoteView(event.getLoggerContextVO());
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
