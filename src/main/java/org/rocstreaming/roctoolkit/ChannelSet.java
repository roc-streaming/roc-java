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

    final int value;

    ChannelSet(int value) {
        this.value = value;
    }
}
