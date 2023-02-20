package org.rocstreaming.roctoolkit;

public class Logger {
    static {
        RocLibrary.loadLibrary();
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
