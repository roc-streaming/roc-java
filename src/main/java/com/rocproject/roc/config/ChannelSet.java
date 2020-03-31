package com.rocproject.roc.config;

/**
 * Channel set.
 */
public enum ChannelSet {

    /**
     * Stereo.
     * Two channels: left and right.
     */
    ROC_CHANNEL_SET_STEREO( getRocChannelSetStereo() );

    private final int value;
    ChannelSet(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocChannelSetStereo();
}
