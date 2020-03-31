package com.rocproject.roc.log;

/**
 *  Log handler.
 *
 *  @see Logger#setCallback(LogHandler)
 */
@FunctionalInterface
public interface LogHandler {

    /**
     *  Log handler function
     * @param level         defines the message level.
     * @param component     defines the component that produces the message.
     * @param message       defines the message text.
     */
    void log(LogLevel level, String component, String message);
}
