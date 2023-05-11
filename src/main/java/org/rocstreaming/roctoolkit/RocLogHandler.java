package org.rocstreaming.roctoolkit;

/**
 *  Log handler.
 */
@FunctionalInterface
interface RocLogHandler {

    /**
     *  Log handler function
     * @param level         defines the message level.
     * @param component     defines the component that produces the message.
     * @param message       defines the message text.
     */
    void log(RocLogLevel level, String component, String message);
}
