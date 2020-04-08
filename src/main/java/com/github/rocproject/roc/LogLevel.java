package com.github.rocproject.roc;

/**
 * Log level.
 * @see Logger#setLevel(LogLevel)
 */
public enum LogLevel {

    /**
     * No messages.
     * Setting this level disables logging completely.
     */
    ROC_LOG_NONE( getRocLogNone() ),

    /**
     * Error messages.
     * Setting this level enables logging only when something goes wrong, e.g. a user
     * operation can't be completed, or there is not enough memory for a new session.
     */
    ROC_LOG_ERROR( getRocLogError() ),

    /**
     * Informational messages.
     * Setting this level enables logging of important high-level events, like binding
     * a new port or creating a new session.
     */
    ROC_LOG_INFO( getRocLogInfo() ),

    /**
     * Debug messages.
     * Setting this level enables logging of debug messages. Doesn't affect performance.
     */
    ROC_LOG_DEBUG( getRocLogDebug() ),

    /**
     * Debug messages (extra verbosity).
     * Setting this level enables verbose tracing. May cause significant slow down.
     */
    ROC_LOG_TRACE( getRocLogTrace() );

    private final int value;
    LogLevel(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocLogNone();
    private static native int getRocLogError();
    private static native int getRocLogInfo();
    private static native int getRocLogDebug();
    private static native int getRocLogTrace();
}
