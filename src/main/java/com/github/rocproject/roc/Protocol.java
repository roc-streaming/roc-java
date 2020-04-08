package com.github.rocproject.roc;

/**
 * Network protocol.
 */
public enum Protocol {

    /**
     * Bare RTP (RFC 3550).
     */
    ROC_PROTO_RTP( getRocProtoRTP() ),

    /**
     * RTP source packet (RFC 3550) + FECFRAME Reed-Solomon footer (RFC 6865) with m=8.
     */
    ROC_PROTO_RTP_RS8M_SOURCE( getRocProtoRTPRS8MSOURCE() ),

    /**
     * FEC repair packet + FECFRAME Reed-Solomon header (RFC 6865) with m=8.
     */
    ROC_PROTO_RS8M_REPAIR( getRocProtoRS8MREPAIR() ),

    /**
     * RTP source packet (RFC 3550) + FECFRAME LDPC-Staircase footer (RFC 6816).
     */
    ROC_PROTO_RTP_LDPC_SOURCE( getRocProtoRTPLDPCSOURCE() ),

    /**
     * FEC repair packet + FECFRAME LDPC-Staircase header (RFC 6816).
     */
    ROC_PROTO_LDPC_REPAIR( getRocProtoLDPCREPAIR() );

    private final int value;
    Protocol(int newValue ) {
        this.value = newValue;
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
