package org.rocstreaming.roctoolkit;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

class RocLibrary {

    private static final Logger LOGGER = Logger.getLogger(RocLibrary.class.getName());
    private static final AtomicBoolean LOADED = new AtomicBoolean();

    static void loadLibrary() {
        if (LOADED.compareAndSet(false, true)) {
            LOGGER.log(Level.FINE, "start loading roc_jni lib");
            System.loadLibrary("roc_jni");
            LOGGER.log(Level.FINE, "roc_jni lib loaded");
        }
    }

    private RocLibrary() {}
}
