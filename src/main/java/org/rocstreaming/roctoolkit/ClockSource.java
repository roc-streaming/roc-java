package org.rocstreaming.roctoolkit;

/**
 * Clock source for sender or receiver.
 */
public enum ClockSource {
    /**
     * Sender or receiver is clocked by external user-defined clock.
     * Write and read operations are non-blocking. The user is responsible
     * to call them in time, according to the external clock.
     */
    EXTERNAL(0),

    /**
     * Sender or receiver is clocked by an internal clock.
     * Write and read operations are blocking. They automatically wait until it's time
     * to process the next bunch of samples according to the configured sample rate.
     */
    INTERNAL(1);


    final int value;

    ClockSource(int value) {
        this.value = value;
    }
}
