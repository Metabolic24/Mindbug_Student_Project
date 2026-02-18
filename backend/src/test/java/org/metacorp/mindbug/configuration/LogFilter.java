package org.metacorp.mindbug.configuration;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * Log filter that should only be used in Logback test configuration
 */
public class LogFilter extends EventEvaluatorBase<ILoggingEvent> {

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException {
        return event.getMessage().contains("Unable to join WS");
    }
}
