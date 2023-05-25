package org.rocstreaming.roctoolkit;

import lombok.*;

/**
 * Sender configuration.
 * <p>
 * RocSenderConfig object can be instantiated with {@link RocSenderConfig#builder()}.
 *
 * @see RocSender
 */
@Getter
@Builder(builderClassName = "ConfigBuilder", toBuilder = true, access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
public class RocSenderConfig {

    /**
     * The rate of the samples in the frames returned to the user.
     * Number of samples per channel per second.
     */
    private int frameSampleRate;

    /**
     * The channel set in the frames returned to the user.
     */
    private ChannelSet frameChannels;

    /**
     * The sample encoding in the frames returned to the user.
     */
    private FrameEncoding frameEncoding;

    /**
     * The rate of the samples in the packets generated by sender.
     * Number of samples per channel per second.
     * If zero, default value is used.
     */
    private int packetSampleRate;

    /**
     * The channel set in the packets generated by sender.
     * If null, default value is used.
     */
    private ChannelSet packetChannels;

    /**
     * The sample encoding in the packets generated by sender.
     * If null, default value is used.
     */
    private PacketEncoding packetEncoding;

    /**
     * The length of the packets produced by sender, in nanoseconds.
     * Number of nanoseconds encoded per packet.
     * The samples written to the sender are buffered until the full
     * packet is accumulated or the sender is flushed or closed.
     * Larger number reduces packet overhead but also increases latency.
     * If zero, default value is used.
     */
    private long packetLength;

    /**
     * Enable packet interleaving.
     * If non-zero, the sender shuffles packets before sending them. This
     * may increase robustness but also increases latency.
     */
    private int packetInterleaving;

    /**
     * Clock source to use.
     * Defines whether write operation will be blocking or non-blocking.
     * If zero, default value is used.
     */
    private ClockSource clockSource;

    /**
     * Resampler backend to use.
     */
    private ResamplerBackend resamplerBackend;

    /**
     * Resampler profile to use.
     * If non-null, the sender employs resampler if the frame sample rate
     * differs from the packet sample rate.
     */
    private ResamplerProfile resamplerProfile;

    /**
     * FEC encoding to use.
     * If non-null, the sender employs a FEC codec to generate redundant
     * packets which may be used on receiver to restore lost packets.
     * This requires both sender and receiver to use two separate source
     * and repair ports.
     */
    private FecEncoding fecEncoding;

    /**
     * Number of source packets per FEC block.
     * Used if some FEC encoding is selected.
     * Larger number increases robustness but also increases latency.
     * If zero, default value is used.
     */
    private int fecBlockSourcePackets;

    /**
     * Number of repair packets per FEC block.
     * Used if some FEC encoding is selected.
     * Larger number increases robustness but also increases traffic.
     * If zero, default value is used.
     */
    private int fecBlockRepairPackets;

    public static ConfigBuilder builder() {
        return new ValidationBuilder();
    }

    private static class ValidationBuilder extends ConfigBuilder {
        @Override
        public RocSenderConfig build() {
            Check.notNegative(super.frameSampleRate, "frameSampleRate");
            Check.notNull(super.frameChannels, "frameChannels");
            Check.notNull(super.frameEncoding, "frameEncoding");
            Check.notNegative(super.packetSampleRate, "packetSampleRate");
            Check.notNegative(super.fecBlockSourcePackets, "fecBlockSourcePackets");
            Check.notNegative(super.fecBlockRepairPackets, "fecBlockRepairPackets");
            return super.build();
        }
    }

}
