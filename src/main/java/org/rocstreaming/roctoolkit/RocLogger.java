package org.rocstreaming.roctoolkit;

import java.util.logging.Level;
import java.util.logging.Logger;

class RocLogger {

    static final Logger LOGGER = Logger.getLogger(RocLogger.class.getName());

    private static final RocLogHandler DEFAULT_HANDLER = (level, component, message) -> {
        Level julLevel = mapLogLevel(level);
        if (LOGGER.isLoggable(julLevel)) {
            LOGGER.logp(julLevel, component, "", message);
        }
    };

    static {
        RocLibrary.loadLibrary();
        setLevel(RocLogLevel.DEBUG); // set debug level and rely on jul to filter messages.
        setHandler(DEFAULT_HANDLER);
        // Jvm could be terminated before roclib, so we need to clear callback to avoid crash
        Runtime.getRuntime().addShutdownHook(new Thread(() -> setHandler(null)));
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
    private native static void setLevel(RocLogLevel level);

    private native static void setHandler(RocLogHandler handler);

    private RocLogger() {
    }
}
