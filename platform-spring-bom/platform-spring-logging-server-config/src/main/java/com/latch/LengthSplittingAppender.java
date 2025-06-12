package com.latch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LengthSplittingAppender extends SplittingAppenderBase<ILoggingEvent> {

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

            LoggingEvent partition = LoggingEventCloner.clone(event);
            Map<String, String> seqMDCPropertyMap = new HashMap<>(event.getMDCPropertyMap());
            seqMDCPropertyMap.put(getSequenceKey(), Integer.toString(i));
            partition.setMDCPropertyMap(seqMDCPropertyMap);
            partition.setMessage(logMessages.get(i));

            splitLogEvents.add(partition);
        }

        return splitLogEvents;
    }

    private List<String> splitString(String str, int chunkSize) {
        int fullChunks = str.length() / chunkSize;
        int remainder = str.length() % chunkSize;

        List<String> results = new ArrayList<>(remainder == 0 ? fullChunks : fullChunks + 1);
        for (int i = 0; i < fullChunks; i++) {
            results.add(str.substring(i*chunkSize, i*chunkSize + chunkSize));
        }
        if (remainder != 0) {
            results.add(str.substring(str.length() - remainder));
        }
        return results;
    }
}
