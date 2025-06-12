package com.latch;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.Iterator;
import java.util.List;

public abstract class SplittingAppenderBase<E> extends UnsynchronizedAppenderBase<E>
        implements AppenderAttachable<E> {

    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<>();

    protected abstract List<E> split(E event);

    protected abstract boolean shouldSplit(E eventObject);

    @Override
    protected void append(E eventObject) {
        if (shouldSplit(eventObject)) {
            split(eventObject).forEach(aai::appendLoopOnAppenders);
        } else {
            aai.appendLoopOnAppenders(eventObject);
        }
    }

    public void addAppender(Appender<E> newAppender) {
        addInfo("Attaching appender named [" + newAppender.getName() + "] to SplittingAppender.");
        aai.addAppender(newAppender);
    }

    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    public Appender<E> getAppender(String name) {
        return aai.getAppender(name);
    }

    public boolean isAttached(Appender<E> eAppender) {
        return aai.isAttached(eAppender);
    }

    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    public boolean detachAppender(Appender<E> eAppender) {
        return aai.detachAppender(eAppender);
    }

    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }
}
