package org.rocstreaming.roctoolkit;

import java.util.logging.Level;

public class Logger {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Logger.class.getName());

    private static final LogHandler DEFAULT_HANDLER = (level, component, message) -> {
        Level julLevel = mapLogLevel(level);
        if (LOGGER.isLoggable(julLevel)) {
            LOGGER.logp(julLevel, component, "", message);
        }
    };

    static {
        RocLibrary.loadLibrary();
        setCallbackNative(DEFAULT_HANDLER);
        // Jvm could be terminated before roclib, so we need to clear callback to avoid crash
        Runtime.getRuntime().addShutdownHook(new Thread(() -> setCallbackNative(null)));
    }

    private static java.util.logging.Level mapLogLevel(LogLevel level) {
        switch (level) {
            case NONE:
                return java.util.logging.Level.OFF;
            case INFO:
                return java.util.logging.Level.INFO;
            case DEBUG:
                return java.util.logging.Level.FINE;
            case TRACE:
                return java.util.logging.Level.FINER;
            default:
                return java.util.logging.Level.SEVERE;
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
     * messages are printed by jul logger.
     * <p>
     * It's guaranteed that the previously set handler, if any, will not be used after this
     * function returns.
     * <p>
     * Handler calls are serialized, so the handler itself doesn't need to be thread-safe
     *
     * @param handler the log handler to set
     */
    public static void setCallback(LogHandler handler) {
        if (handler == null) {
            setCallbackNative(DEFAULT_HANDLER);
        } else {
            LogHandler wrapper = (level, component, message) -> {
                try {
                    handler.log(level, component, message);
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Logger failed to log message", e);
                }
            };
            setCallbackNative(wrapper);
        }
    }

    private native static void setCallbackNative(LogHandler handler);

    private Logger() {
    }
}
