package com.github.rocproject.roc;

import java.util.function.Supplier;

/**
 * Channel set.
 */
public enum ChannelSet {

    /**
     * Stereo.
     * Two channels: left and right.
     */
    STEREO( ChannelSet::getRocChannelSetStereo );

    private final int value;
    ChannelSet(Supplier<Integer> value) {
        RocLibrary.loadLibrary();
        this.value = value.get();
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocChannelSetStereo();
}
