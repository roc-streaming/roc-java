package org.rocstreaming.roctoolkit;

import java.util.function.Supplier;

/**
 * Log level.
 * @see Logger#setLevel(LogLevel)
 */
public enum LogLevel {

    /**
     * No messages.
     * Setting this level disables logging completely.
     */
    NONE( LogLevel::getRocLogNone ),

    /**
     * Error messages.
     * Setting this level enables logging only when something goes wrong, e.g. a user
     * operation can't be completed, or there is not enough memory for a new session.
     */
    ERROR( LogLevel::getRocLogError ),

    /**
     * Informational messages.
     * Setting this level enables logging of important high-level events, like binding
     * a new port or creating a new session.
     */
    INFO( LogLevel::getRocLogInfo ),

    /**
     * Debug messages.
     * Setting this level enables logging of debug messages. Doesn't affect performance.
     */
    DEBUG( LogLevel::getRocLogDebug ),

    /**
     * Debug messages (extra verbosity).
     * Setting this level enables verbose tracing. May cause significant slow down.
     */
    TRACE( LogLevel::getRocLogTrace );

    private final int value;
    LogLevel(Supplier<Integer> value) {
        RocLibrary.loadLibrary();
        this.value = value.get();
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
