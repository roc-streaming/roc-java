package org.rocstreaming.roctoolkit;

import java.util.logging.Level;
import java.util.logging.Logger;

class RocLogger {

    static final Logger LOGGER = Logger.getLogger(RocLogger.class.getName());

    private static final RocLogHandler HANDLER = (level, component, message) -> {
        Level julLevel = mapLogLevel(level);
        if (LOGGER.isLoggable(julLevel)) {
            LOGGER.logp(julLevel, component, "", message);
        }
    };

    static {
        RocLibrary.loadLibrary();
        // Set debug level and rely on jul to filter messages.
        nativeSetLevel(RocLogLevel.DEBUG);
        // Register callback for libroc log messages.
        nativeSetHandler(HANDLER);
        // JVM could be terminated before libroc, so we need to unregister Java callback
        // from libroc before shutting down, to avoid crash.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nativeSetHandler(null)));
    }

    private RocLogger() {
    }

    private static Level mapLogLevel(RocLogLevel level) {
        switch (level) {
            case NONE:
                return Level.OFF;
            case ERROR:
                return Level.SEVERE;
            case INFO:
                return Level.INFO;
            case DEBUG:
                return Level.FINE;
            case TRACE:
                return Level.FINEST;
            default:
                break;
        }
        // Can't happen.
        return Level.FINE;
    }

    private native static void nativeSetLevel(RocLogLevel level);
    private native static void nativeSetHandler(RocLogHandler handler);
}
