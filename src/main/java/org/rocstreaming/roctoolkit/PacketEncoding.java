package org.rocstreaming.roctoolkit;

import java.util.function.Supplier;

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
    AVP_L16( PacketEncoding::getRocPacketEncodingAVPL16 );

    private final int value;
    PacketEncoding(Supplier<Integer> value) {
        RocLibrary.loadLibrary();
        this.value = value.get();
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocPacketEncodingAVPL16();
}
