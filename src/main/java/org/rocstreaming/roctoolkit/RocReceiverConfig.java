package org.rocstreaming.roctoolkit;

import lombok.*;

/**
 * Receiver configuration.
 * <p>
 * RocReceiverConfig object can be instantiated with {@link RocReceiverConfig#builder()}.
 *
 * @see RocReceiver
 */
@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
@ToString
@EqualsAndHashCode
public class RocReceiverConfig {

    /**
     * The rate of the samples in the frames returned to the user.
     * Number of samples per channel per second.
     * Should be set to a positive value.
     */
    private int frameSampleRate;

    /**
     * The channel set in the frames returned to the user.
     * Should be set to a non-null value.
     */
    private ChannelSet frameChannels;

    /**
     * The sample encoding in the frames returned to the user.
     * Should be set to a non-null value.
     */
    private FrameEncoding frameEncoding;

    /**
     * Clock source to use.
     * Defines whether read operation will be blocking or non-blocking.
     * If null or unset, default value is used.
     */
    private ClockSource clockSource;

    /**
     * Resampler backend to use.
     * If null or unset, default value is used.
     */
    private ResamplerBackend resamplerBackend;

    /**
     * Resampler profile to use.
     * If null or unset, default value is used.
     * If resampling is enabled, the receiver employs resampler for two purposes:
     * <ul>
     *     <li>
     *         adjust the sender clock to the receiver clock, which
     *         may differ a bit
     *     </li>
     *     <li>
     *         convert the packet sample rate to the frame sample
     *         rate if they are different
     *     </li>
     * </ul>
     */
    private ResamplerProfile resamplerProfile;

    /**
     * Target latency, in nanoseconds.
     * The session will not start playing until it accumulates the
     * requested latency.
     * Then, if resampler is enabled, the session will adjust its clock
     * to keep actual latency as close as possible to the target latency.
     * If zero or unset, default value is used.
     * Should not be negative.
     */
    private long targetLatency;

    /**
     * Maximum delta between current and target latency, in nanoseconds.
     * If current latency becomes larger than the target latency plus
     * this value, the session is terminated.
     * If zero or unset, default value is used.
     * Should not be negative.
     */
    private long maxLatencyOverrun;

    /**
     * Maximum delta between target and current latency, in nanoseconds.
     * If current latency becomes smaller than the target latency minus
     * this value, the session is terminated.
     * May be larger than the target latency because current latency may
     * be negative, which means that the playback run ahead of the last
     * packet received from network.
     * If zero or unset, default value is used.
     * Should not be negative.
     */
    private long maxLatencyUnderrun;

    /**
     * Timeout for the lack of playback, in nanoseconds.
     * If there is no playback during this period, the session is terminated.
     * This mechanism allows to detect dead, hanging, or broken clients
     * generating invalid packets.
     * If zero or unset, default value is used.
     * If negative, the timeout is disabled.
     */
    private long noPlaybackTimeout;

    /**
     * Timeout for broken playback, in nanoseconds.
     * If there the playback is considered broken during this period,
     * the session is terminated. The playback is broken if there is
     * a breakage detected at every <code>breakageDetectionWindow</code>
     * during <code>brokenPlaybackTimeout</code>.
     * This mechanism allows to detect vicious circles like when all
     * client packets are a bit late and receiver constantly drops them
     * producing unpleasant noise.
     * If zero or unset, default value is used.
     * If negative, the timeout is disabled.
     */
    private long brokenPlaybackTimeout;

    /**
     * Breakage detection window, in nanoseconds.
     * If zero or unset, default value is used.
     * Should not be negative.
     */
    private long breakageDetectionWindow;

    public static RocReceiverConfig.Builder builder() {
        return new ValidationBuilder();
    }

    private static class ValidationBuilder extends RocReceiverConfig.Builder {
        @Override
        public RocReceiverConfig build() {
            Check.notNegative(super.frameSampleRate, "frameSampleRate");
            Check.notNull(super.frameChannels, "frameChannels");
            Check.notNull(super.frameEncoding, "frameEncoding");
            Check.notNegative(super.targetLatency, "targetLatency");
            Check.notNegative(super.maxLatencyOverrun, "maxLatencyOverrun");
            Check.notNegative(super.maxLatencyUnderrun, "maxLatencyUnderrun");
            Check.notNegative(super.breakageDetectionWindow, "breakageDetectionWindow");
            return super.build();
        }
    }
}
