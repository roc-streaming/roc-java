package com.github.rocproject.roc;

import java.util.function.Supplier;

/**
 * Frame encoding.
 */
public enum FrameEncoding {

    /**
     * PCM floats.
     * Uncompressed samples coded as floats in range [-1; 1].
     * Channels are interleaved, e.g. two channels are encoded as "L R L R ...".
     */
    PCM_FLOAT( FrameEncoding::getRocFrameEncodingPCMFloat );

    private final int value;
    FrameEncoding(Supplier<Integer> value) {
        RocLibrary.loadLibrary();
        this.value = value.get();
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocFrameEncodingPCMFloat();
}
