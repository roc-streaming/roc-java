package org.rocstreaming.roctoolkit;

import java.util.logging.Level;

public class Logger {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Logger.class.getName());

    static {
        RocLibrary.loadLibrary();
        setCallback((level, component, message) -> {
            Level julLevel = mapLogLevel(level);
            if (LOGGER.isLoggable(julLevel)) {
                LOGGER.logp(julLevel, component, "", message);
            }
        });
    }

    private static java.util.logging.Level mapLogLevel(LogLevel level) {
        switch (level) {
            case NONE:
                return java.util.logging.Level.OFF;
            case ERROR:
                return java.util.logging.Level.SEVERE;
            case INFO:
                return java.util.logging.Level.INFO;
            case DEBUG:
                return java.util.logging.Level.FINE;
            case TRACE:
                return java.util.logging.Level.FINER;
            default:
                throw new IllegalArgumentException("Unknown log level: " + level);
        }
    }

    /**
     * Set maximum log level.
     * <p>
     * Messages with log levels higher than param level will be dropped.
     * By default the log level is set to {@link LogLevel#ERROR ERROR}.
     *
     * @param level maximum log level.
     */
    public native static void setLevel(LogLevel level);

    /**
     * Set log handler.
     * <p>
     * If <code>handler</code> is not null, messages are passed to the handler. Otherwise,
     * messages are printed to stderr. By default the log handler is set to null.
     * <p>
     * It's guaranteed that the previously set handler, if any, will not be used after this
     * function returns.
     * <p>
     * Handler calls are serialized, so the handler itself doesn't need to be thread-safe
     *
     * @param handler the log handler to set
     */
    public native static void setCallback(LogHandler handler);

    private Logger() {
    }
}
