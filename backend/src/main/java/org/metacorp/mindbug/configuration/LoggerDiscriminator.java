package org.metacorp.mindbug.configuration;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;
import lombok.Getter;

import java.util.UUID;

public class LoggerDiscriminator implements Discriminator<ILoggingEvent> {

    private static final String KEY = "loggerName";
    private static final String DEFAULT_LOGGER = "mindbug";

    @Getter
    private boolean started;

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        try {
            UUID.fromString(iLoggingEvent.getLoggerName());
            return iLoggingEvent.getLoggerName();
        } catch (IllegalArgumentException e) {
            return DEFAULT_LOGGER;
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }
}
