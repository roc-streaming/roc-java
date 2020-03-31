package com.rocproject.roc.config;

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
    ROC_PACKET_ENCODING_AVP_L16( getRocPacketEncodingAVPL16() );

    private final int value;
    PacketEncoding(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocPacketEncodingAVPL16();
}
