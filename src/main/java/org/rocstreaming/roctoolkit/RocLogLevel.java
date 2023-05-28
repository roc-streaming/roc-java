package org.rocstreaming.roctoolkit;

/**
 * Log level.
 *
 * @see RocLogger#setLevel(RocLogLevel)
 */
public enum RocLogLevel {

    /**
     * No messages.
     * Setting this level disables logging completely.
     */
    NONE(0),

    /**
     * Error messages.
     * Setting this level enables logging only when something goes wrong, e.g. a user
     * operation can't be completed, or there is not enough memory for a new session.
     */
    ERROR(1),

    /**
     * Informational messages.
     * Setting this level enables logging of important high-level events, like binding
     * a new port or creating a new session.
     */
    INFO(2),

    /**
     * Debug messages.
     * Setting this level enables logging of debug messages. Doesn't affect performance.
     */
    DEBUG(3),

    /**
     * Debug messages (extra verbosity).
     * Setting this level enables verbose tracing. May cause significant slow down.
     */
    TRACE(4);

    final int value;

    RocLogLevel(int value) {
        this.value = value;
    }
}
