package com.github.rocproject.roc;

import java.util.function.Supplier;

/**
 * Network protocol.
 */
public enum Protocol {

    /**
     * Bare RTP (RFC 3550).
     */
    RTP( Protocol::getRocProtoRTP ),

    /**
     * RTP source packet (RFC 3550) + FECFRAME Reed-Solomon footer (RFC 6865) with m=8.
     */
    RTP_RS8M_SOURCE( Protocol::getRocProtoRTPRS8MSOURCE ),

    /**
     * FEC repair packet + FECFRAME Reed-Solomon header (RFC 6865) with m=8.
     */
    RS8M_REPAIR( Protocol::getRocProtoRS8MREPAIR ),

    /**
     * RTP source packet (RFC 3550) + FECFRAME LDPC-Staircase footer (RFC 6816).
     */
    RTP_LDPC_SOURCE( Protocol::getRocProtoRTPLDPCSOURCE ),

    /**
     * FEC repair packet + FECFRAME LDPC-Staircase header (RFC 6816).
     */
    LDPC_REPAIR( Protocol::getRocProtoLDPCREPAIR );

    private final int value;
    Protocol(Supplier<Integer> value) {
        RocLibrary.loadLibrary();
        this.value = value.get();
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocProtoRTP();
    private static native int getRocProtoRTPRS8MSOURCE();
    private static native int getRocProtoRS8MREPAIR();
    private static native int getRocProtoRTPLDPCSOURCE();
    private static native int getRocProtoLDPCREPAIR();
}
