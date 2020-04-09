package com.github.rocproject.roc;

/**
 * Channel set.
 */
public enum ChannelSet {

    /**
     * Stereo.
     * Two channels: left and right.
     */
    STEREO( getRocChannelSetStereo() );

    private final int value;
    ChannelSet(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocChannelSetStereo();

    static {
        RocLibrary.loadLibrary();
    }
}
