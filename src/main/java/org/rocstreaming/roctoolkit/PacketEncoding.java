package org.rocstreaming.roctoolkit;

/**
 * Packet encoding.
 */
public enum PacketEncoding {

    /**
     * PCM signed 16-bit.
     * "L16" encoding from RTP A/V Profile (RFC 3551).
     * Uncompressed samples coded as interleaved 16-bit signed big-endian
     * integers in two's complement notation.
     */
    AVP_L16(2);

    private final int value;

    PacketEncoding(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
