package org.rocstreaming.roctoolkit;


/**
 * Forward Error Correction encoding.
 */
public enum FecEncoding {

    /**
     * No FEC encoding.
     * Compatible with {@link Protocol#RTP RTP} protocol.
     */
    DISABLE(-1),

    /**
     * Default FEC encoding.
     * Current default is {@link FecEncoding#RS8M RS8M}.
     */
    DEFAULT(0),

    /**
     * Reed-Solomon FEC encoding (RFC 6865) with m=8.
     * Good for small block sizes (below 256 packets).
     * Compatible with {@link Protocol#RTP_RS8M_SOURCE RTP_RS8M_SOURCE}
     * and {@link Protocol#RS8M_REPAIR RS8M_REPAIR} protocols for source
     * and repair endpoints.
     */
    RS8M(1),

    /**
     * LDPC-Staircase FEC encoding (RFC 6816).
     * Good for large block sizes (above 1024 packets).
     * Compatible with {@link Protocol#RTP_LDPC_SOURCE RTP_LDPC_SOURCE}
     * and {@link Protocol#LDPC_REPAIR LDPC_REPAIR} protocols for source
     * and repair endpoints.
     */
    LDPC_STAIRCASE(2);

    private final int value;

    FecEncoding(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
