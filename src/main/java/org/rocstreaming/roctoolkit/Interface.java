package org.rocstreaming.roctoolkit;

/**
 * Network interface.
 * <p>
 * Interface is a way to access the peer (sender or receiver) via network.
 * <p>
 * Each peer slot has multiple interfaces, one of each type. The user interconnects
 * peers by binding one of the first peer's interfaces to an URI and then connecting the
 * corresponding second peer's interface to that URI.
 * <p>
 * A URI is represented by {@link Endpoint} object.
 * <p>
 * The interface defines the type of the communication with the remote peer and the
 * set of protocols (URI schemes) that can be used with this particular interface.
 * <p>
 * {@link Interface#CONSOLIDATED CONSOLIDATED} is an interface for high-level protocols which
 * automatically manage all necessary communication: transport streams, control messages,
 * parameter negotiation, etc. When a consolidated connection is established, peers may
 * automatically setup lower-level interfaces like {@link Interface#AUDIO_SOURCE AUDIO_SOURCE},
 * {@link Interface#AUDIO_REPAIR AUDIO_REPAIR}, and {@link Interface#AUDIO_CONTROL AUDIO_CONTROL}.
 * <p>
 * {@link Interface#CONSOLIDATED CONSOLIDATED} is mutually exclusive with lower-level interfaces.
 * In most cases, the user needs only {@link Interface#CONSOLIDATED CONSOLIDATED}. However, the
 * lower-level intarfaces may be useful if an external signaling mechanism is used or for
 * compatibility with third-party software.
 * <p>
 * {@link Interface#AUDIO_SOURCE AUDIO_SOURCE} and {@link Interface#AUDIO_REPAIR AUDIO_REPAIR} are lower-level
 * unidirectional transport-only interfaces. The first is used to transmit audio stream,
 * and the second is used to transmit redundant repair stream, if FEC is enabled.
 * <p>
 * {@link Interface#AUDIO_CONTROL AUDIO_CONTROL} is a lower-level interface for control streams.
 * If you use {@link Interface#AUDIO_SOURCE AUDIO_SOURCE} and {@link Interface#AUDIO_REPAIR AUDIO_REPAIR}, you
 * usually also need to use {@link Interface#AUDIO_CONTROL AUDIO_CONTROL} to enable carrying additional
 * non-transport information.
 */
public enum Interface {

    /**
     * Interface that consolidates all types of streams (source, repair, control).
     * <p>
     * Allowed operations:
     * - bind    (sender, receiver)
     * - connect (sender, receiver)
     * <p>
     * Allowed protocols:
     * - {@link Protocol#RTSP RTSP}
     */
    CONSOLIDATED(1),

    /**
     * Interface for audio stream source data.
     * <p>
     * Allowed operations:
     * - bind    (receiver)
     * - connect (sender)
     * <p>
     * Allowed protocols:
     * - {@link Protocol#RTP RTP}
     * - {@link Protocol#RTP_RS8M_SOURCE RTP_RS8M_SOURCE}
     * - {@link Protocol#RTP_LDPC_SOURCE RTP_LDPC_SOURCE}
     */
    AUDIO_SOURCE(11),

    /**
     * Interface for audio stream repair data.
     * <p>
     * Allowed operations:
     * - bind    (receiver)
     * - connect (sender)
     * <p>
     * Allowed protocols:
     * - {@link Protocol#RS8M_REPAIR RS8M_REPAIR}
     * - {@link Protocol#LDPC_REPAIR LDPC_REPAIR}
     */
    AUDIO_REPAIR(12),

    /**
     * Interface for audio control messages.
     * <p>
     * Allowed operations:
     * - bind    (sender, receiver)
     * - connect (sender, receiver)
     * <p>
     * Allowed protocols:
     * - {@link Protocol#RTCP RTCP}
     */
    AUDIO_CONTROL(13);

    private final int value;

    Interface(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
