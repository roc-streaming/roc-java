package org.rocstreaming.roctoolkit;

/**
 * Network protocol.
 */
public enum Protocol {

    /**
     * RTSP 1.0 (RFC 2326) or RTSP 2.0 (RFC 7826).
     * <p>
     * Interfaces:
     * - {@link Interface#CONSOLIDATED}
     * <p>
     * Transports:
     * - for signaling: TCP
     * - for media: RTP and RTCP over UDP or TCP
     */
    RTSP(10),

    /**
     * RTP over UDP (RFC 3550).
     * <p>
     * Interfaces:
     * - {@link Interface#AUDIO_SOURCE}
     * <p>
     * Transports:
     * - UDP
     * <p>
     * Audio encodings:
     * - {@link PacketEncoding#AVP_L16}
     * <p>
     * FEC encodings:
     * - none
     */
    RTP(20),

    /**
     * RTP source packet (RFC 3550) + FECFRAME Reed-Solomon footer (RFC 6865) with m=8.
     * <p>
     * Interfaces:
     * - {@link Interface#AUDIO_SOURCE}
     * <p>
     * Transports:
     * - UDP
     * <p>
     * Audio encodings:
     * - similar to {@link Protocol#RTP}
     * <p>
     * FEC encodings:
     * - {@link FecEncoding#RS8M}
     */
    RTP_RS8M_SOURCE(30),

    /**
     * FEC repair packet + FECFRAME Reed-Solomon header (RFC 6865) with m=8.
     * <p>
     * Interfaces:
     * - {@link Interface#AUDIO_REPAIR}
     * <p>
     * Transports:
     * - UDP
     * <p>
     * FEC encodings:
     * - {@link FecEncoding#RS8M}
     */
    RS8M_REPAIR(31),

    /**
     * RTP source packet (RFC 3550) + FECFRAME LDPC-Staircase footer (RFC 6816).
     * <p>
     * Interfaces:
     * - {@link Interface#AUDIO_SOURCE}
     * <p>
     * Transports:
     * - UDP
     * <p>
     * Audio encodings:
     * - similar to {@link Protocol#RTP}
     * <p>
     * FEC encodings:
     * - {@link FecEncoding#LDPC_STAIRCASE}
     */
    RTP_LDPC_SOURCE(32),

    /**
     * FEC repair packet + FECFRAME LDPC-Staircase header (RFC 6816).
     * <p>
     * Interfaces:
     * - {@link Interface#AUDIO_REPAIR}
     * <p>
     * Transports:
     * - UDP
     * <p>
     * FEC encodings:
     * - {@link FecEncoding#LDPC_STAIRCASE}
     */
    LDPC_REPAIR(33),

    /**
     * RTCP over UDP (RFC 3550).
     * <p>
     * Interfaces:
     * - {@link Interface#AUDIO_CONTROL}
     * <p>
     * Transports:
     * - UDP
     */
    RTCP(70);

    final int value;

    Protocol(int value) {
        this.value = value;
    }

    @SuppressWarnings("unused") // used by JNI
    private static Protocol getByValue(int value) {
        for (Protocol protocol : values()) {
            if (value == protocol.value) {
                return protocol;
            }
        }
        return null;
    }
}
