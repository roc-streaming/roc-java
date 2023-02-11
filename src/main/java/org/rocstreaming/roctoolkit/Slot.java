package org.rocstreaming.roctoolkit;

/**
 * Network slot.
 * <p>
 * A peer (sender or receiver) may have multiple slots, which may be independently
 * bound or connected. You can use multiple slots on sender to connect it to multiple
 * receiver addresses, and you can use multiple slots on receiver to bind it to
 * multiple receiver address.
 * <p>
 * Slots are numbered from zero and are created implicitly. Just specify slot index
 * when binding or connecting endpoint, and slot will be automatically created if it
 * was not created yet.
 * <p>
 * In simple cases, just use {@link Slot#DEFAULT}.
 * <p>
 * Each slot has its own set of interfaces, dedicated to different kinds of endpoints.
 * See {@link Interface} for details.
 */
public class Slot {

    public static final Slot DEFAULT = new Slot(0);

    private final int value;

    public Slot(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
