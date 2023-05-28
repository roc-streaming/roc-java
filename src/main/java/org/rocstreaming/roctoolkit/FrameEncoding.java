package org.rocstreaming.roctoolkit;

/**
 * Frame encoding.
 */
public enum FrameEncoding {

    /**
     * PCM floats.
     * Uncompressed samples coded as floats in range [-1; 1].
     * Channels are interleaved, e.g. two channels are encoded as "L R L R ...".
     */
    PCM_FLOAT(1);

    final int value;

    FrameEncoding(int value) {
        this.value = value;
    }
}
