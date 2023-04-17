package org.rocstreaming.roctoolkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RocLogger {

    private static final Logger LOGGER = Logger.getLogger(RocLogger.class.getName());

    private static final RocLogHandler DEFAULT_HANDLER = (level, component, message) -> {
        Level julLevel = mapLogLevel(level);
        if (LOGGER.isLoggable(julLevel)) {
            LOGGER.logp(julLevel, component, "", message);
        }
    };

    static {
        RocLibrary.loadLibrary();
        setHandlerNative(DEFAULT_HANDLER);
        // Jvm could be terminated before roclib, so we need to clear callback to avoid crash
        Runtime.getRuntime().addShutdownHook(new Thread(() -> setHandlerNative(null)));
    }

    private static Level mapLogLevel(RocLogLevel level) {
        switch (level) {
            case NONE:
                return Level.OFF;
            case INFO:
                return Level.INFO;
            case DEBUG:
                return Level.FINE;
            case TRACE:
                return Level.FINER;
            default:
                return Level.SEVERE;
        }
    }

    /**
     * Set maximum log level.
     * <p>
     * Messages with log levels higher than param level will be dropped.
     * By default the log level is set to {@link RocLogLevel#ERROR ERROR}.
     *
     * @param level maximum log level.
     */
    public native static void setLevel(RocLogLevel level);

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
    public static void setHandler(RocLogHandler handler) {
        if (handler == null) {
            setHandlerNative(DEFAULT_HANDLER);
        } else {
            RocLogHandler wrapper = (level, component, message) -> {
                try {
                    handler.log(level, component, message);
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Logger failed to log message", e);
                }
            };
            setHandlerNative(wrapper);
        }
    }

    private native static void setHandlerNative(RocLogHandler handler);

    private RocLogger() {
    }
}
