package com.rocproject.roc.config;

/**
 * Forward Error Correction code.
 */
public enum FecCode {

    /** No FEC code.
     * Compatible with {@link Protocol#ROC_PROTO_RTP ROC_PROTO_RTP} protocol.
     */
    ROC_FEC_DISABLE( getRocFecCodeDisable() ),

    /**
     * Default FEC code.
     * Current default is {@link FecCode#ROC_FEC_RS8M ROC_FEC_RS8M}.
     */
    ROC_FEC_DEFAULT( getRocFecCodeDefault() ),

    /**
     * Reed-Solomon FEC code (RFC 6865) with m=8.
     * Good for small block sizes (below 256 packets).
     * Compatible with {@link Protocol#ROC_PROTO_RTP_RS8M_SOURCE ROC_PROTO_RTP_RS8M_SOURCE}
     * and {@link Protocol#ROC_PROTO_RS8M_REPAIR ROC_PROTO_RS8M_REPAIR} protocols for source
     * and repair ports.
     */
    ROC_FEC_RS8M( getRocFecCodeRS8M() ),

    /**
     * LDPC-Staircase FEC code (RFC 6816).
     * Good for large block sizes (above 1024 packets).
     * Compatible with {@link Protocol#ROC_PROTO_RTP_LDPC_SOURCE ROC_PROTO_RTP_LDPC_SOURCE}
     * and {@link Protocol#ROC_PROTO_LDPC_REPAIR ROC_PROTO_LDPC_REPAIR} protocols for source
     * and repair ports.
     */
    ROC_FEC_LDPC_STAIRCASE( getRocFecCodeLDPCSTAIRCASE() );

    private final int value;
    FecCode(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocFecCodeDisable();
    private static native int getRocFecCodeDefault();
    private static native int getRocFecCodeRS8M();
    private static native int getRocFecCodeLDPCSTAIRCASE();
}
