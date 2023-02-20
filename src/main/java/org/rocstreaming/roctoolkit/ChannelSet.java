package org.rocstreaming.roctoolkit;

/**
 * Channel set.
 */
public enum ChannelSet {

    /**
     * Stereo.
     * Two channels: left and right.
     */
    STEREO(0x3);

    private final int value;

    ChannelSet(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
