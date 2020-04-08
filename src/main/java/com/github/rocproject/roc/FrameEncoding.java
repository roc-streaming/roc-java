package com.github.rocproject.roc;

/**
 * Frame encoding.
 */
public enum FrameEncoding {

    /**
     * PCM floats.
     * Uncompressed samples coded as floats in range [-1; 1].
     * Channels are interleaved, e.g. two channels are encoded as "L R L R ...".
     */
    ROC_FRAME_ENCODING_PCM_FLOAT( getRocFrameEncodingPCMFloat() );

    private final int value;
    FrameEncoding(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocFrameEncodingPCMFloat();
}
