package org.rocstreaming.roctoolkit;

/**
 * Receiver configuration.
 *
 * ReceiverConfig object can be instantiated with {@link ReceiverConfig.Builder ReceiverConfig.Builder} objects.
 *
 * @see Receiver
 * @see ReceiverConfig.Builder
 */
public class ReceiverConfig {

    private int frameSampleRate;
    private ChannelSet frameChannels;
    private FrameEncoding frameEncoding;
    private boolean automaticTiming;
    private ResamplerProfile resamplerProfile;
    private long targetLatency;
    private long maxLatencyOverrun;
    private long maxLatencyUnderrun;
    private long noPlaybackTimeout;
    private long brokenPlaybackTimeout;
    private long breakageDetectionWindow;

    private ReceiverConfig(int frameSampleRate,
                           ChannelSet frameChannels,
                           FrameEncoding frameEncoding,
                           boolean automaticTiming,
                           ResamplerProfile resamplerProfile,
                           long targetLatency,
                           long maxLatencyOverrun,
                           long maxLatencyUnderrun,
                           long noPlaybackTimeout,
                           long brokenPlaybackTimeout,
                           long breakageDetectionWindow) {
        this.frameSampleRate = frameSampleRate;
        this.frameChannels = frameChannels;
        this.frameEncoding = frameEncoding;
        this.automaticTiming = automaticTiming;
        this.resamplerProfile = resamplerProfile;
        this.targetLatency = targetLatency;
        this.maxLatencyOverrun = maxLatencyOverrun;
        this.maxLatencyUnderrun = maxLatencyUnderrun;
        this.noPlaybackTimeout = noPlaybackTimeout;
        this.brokenPlaybackTimeout = brokenPlaybackTimeout;
        this.breakageDetectionWindow = breakageDetectionWindow;
    }

    /**
     *  Builder class for {@link ReceiverConfig ReceiverConfig} objects
     * @see ReceiverConfig
     */
    public static class Builder {
        private int frameSampleRate;
        private ChannelSet frameChannels;
        private FrameEncoding frameEncoding;
        private boolean automaticTiming;
        private ResamplerProfile resamplerProfile;
        private long targetLatency;
        private long maxLatencyOverrun;
        private long maxLatencyUnderrun;
        private long noPlaybackTimeout;
        private long brokenPlaybackTimeout;
        private long breakageDetectionWindow;

        /**
         * Create a Builder object for building {@link ReceiverConfig ReceiverConfig}
         *
         * @param frameSampleRate   The rate of the samples in the frames returned to the user.
         *                          Number of samples per channel per second.
         * @param frameChannels     The channel set in the frames returned to the user.
         * @param frameEncoding     The sample encoding in the frames returned to the user.
         */
        public Builder(int frameSampleRate, ChannelSet frameChannels, FrameEncoding frameEncoding) {
            this.frameSampleRate = frameSampleRate;
            this.frameChannels = frameChannels;
            this.frameEncoding = frameEncoding;
        }

        /**
         * @param automaticTiming   Enable automatic timing.
         *                          If non-zero, the receiver read operation restricts the read
         *                          rate according to the <code>frameSampleRate</code> parameter.
         *                          If zero, no restrictions are applied.
         * @return this Builder
         */
        public Builder automaticTiming(boolean automaticTiming) {
            this.automaticTiming = automaticTiming;
            return this;
        }

        /**
         * @param resamplerProfile  Resampler profile to use.
         *                          If non-zero, the receiver employs resampler for two purposes:
         *                          <ul>
         *                              <li>
         *                                  adjust the sender clock to the receiver clock, which
         *                                  may differ a bit
         *                              </li>
         *                              <li>
         *                                  convert the packet sample rate to the frame sample
         *                                  rate if they are different
         *                              </li>
         *                          </ul>
         * @return this Builder
         */
        public Builder resamplerProfile(ResamplerProfile resamplerProfile) {
            this.resamplerProfile = resamplerProfile;
            return this;
        }

        /**
         * @param targetLatency     Target latency, in nanoseconds.
         *                          The session will not start playing until it accumulates the
         *                          requested latency.
         *                          Then, if resampler is enabled, the session will adjust its clock
         *                          to keep actual latency as close as close as possible to the target
         *                          latency.
         *                          If zero, default value is used.
         * @return this Builder
         */
        public Builder targetLatency(long targetLatency) {
            this.targetLatency = targetLatency;
            return this;
        }

        /**
         * @param maxLatencyOverrun Maximum delta between current and target latency, in nanoseconds.
         *                          If current latency becomes larger than the target latency plus
         *                          this value, the session is terminated.
         *                          If zero, default value is used.
         * @return this Builder
         */
        public Builder maxLatencyOverrun(long maxLatencyOverrun) {
            this.maxLatencyOverrun = maxLatencyOverrun;
            return this;
        }

        /**
         * @param maxLatencyUnderrun Maximum delta between target and current latency, in nanoseconds.
         *                           If current latency becomes smaller than the target latency minus
         *                           this value, the session is terminated.
         *                           May be larger than the target latency because current latency may
         *                           be negative, which means that the playback run ahead of the last
         *                           packet received from network.
         *                           If zero, default value is used.
         * @return this Builder
         */
        public Builder maxLatencyUnderrun(long maxLatencyUnderrun) {
            this.maxLatencyUnderrun = maxLatencyUnderrun;
            return this;
        }

        /**
         * @param noPlaybackTimeout  Timeout for the lack of playback, in nanoseconds.
         *                           If there is no playback during this period, the session is terminated.
         *                           This mechanism allows to detect dead, hanging, or broken clients
         *                           generating invalid packets.
         *                           If zero, default value is used. If negative, the timeout is disabled.
         * @return this Builder
         */
        public Builder noPlaybackTimeout(long noPlaybackTimeout) {
            this.noPlaybackTimeout = noPlaybackTimeout;
            return this;
        }

        /**
         * @param brokenPlaybackTimeout Timeout for broken playback, in nanoseconds.
         *                              If there the playback is considered broken during this period,
         *                              the session is terminated. The playback is broken if there is
         *                              a breakage detected at every <code>breakageDetectionWindow</code>
         *                              during <code>brokenPlaybackTimeout</code>.
         *                              This mechanism allows to detect vicious circles like when all
         *                              client packets are a bit late and receiver constantly drops them
         *                              producing unpleasant noise.
         *                              If zero, default value is used. If negative, the timeout is disabled.
         * @return this Builder
         */
        public Builder brokenPlaybackTimeout(long brokenPlaybackTimeout) {
            this.brokenPlaybackTimeout = brokenPlaybackTimeout;
            return this;
        }

        /**
         * @param breakageDetectionWindow Breakage detection window, in nanoseconds.
         *                                If zero, default value is used.
         *                                  @see Builder#brokenPlaybackTimeout
         * @return this Builder
         */
        public Builder breakageDetectionWindow(long breakageDetectionWindow) {
            this.breakageDetectionWindow = breakageDetectionWindow;
            return this;
        }

        /**
         *  Build the {@link ReceiverConfig ReceiverConfig} object with <code>Builder</code> parameters.
         * @return the new {@link ReceiverConfig ReceiverConfig}
         */
        public ReceiverConfig build() {
            return new ReceiverConfig(frameSampleRate, frameChannels, frameEncoding, automaticTiming,
                                    resamplerProfile, targetLatency, maxLatencyOverrun, maxLatencyUnderrun,
                                    noPlaybackTimeout, brokenPlaybackTimeout, breakageDetectionWindow);
        }
    }

    public int getFrameSampleRate() {
        return frameSampleRate;
    }

    public void setFrameSampleRate(int frameSampleRate) {
        this.frameSampleRate = frameSampleRate;
    }

    public ChannelSet getFrameChannels() {
        return frameChannels;
    }

    public void setFrameChannels(ChannelSet frameChannels) {
        this.frameChannels = frameChannels;
    }

    public FrameEncoding getFrameEncoding() {
        return frameEncoding;
    }

    public void setFrameEncoding(FrameEncoding frameEncoding) {
        this.frameEncoding = frameEncoding;
    }

    public boolean getAutomaticTiming() {
        return automaticTiming;
    }

    public void setAutomaticTiming(boolean automaticTiming) {
        this.automaticTiming = automaticTiming;
    }

    public ResamplerProfile getResamplerProfile() {
        return resamplerProfile;
    }

    public void setResamplerProfile(ResamplerProfile resamplerProfile) {
        this.resamplerProfile = resamplerProfile;
    }

    public long getTargetLatency() {
        return targetLatency;
    }

    public void setTargetLatency(long targetLatency) {
        this.targetLatency = targetLatency;
    }

    public long getMaxLatencyOverrun() {
        return maxLatencyOverrun;
    }

    public void setMaxLatencyOverrun(long maxLatencyOverrun) {
        this.maxLatencyOverrun = maxLatencyOverrun;
    }

    public long getMaxLatencyUnderrun() {
        return maxLatencyUnderrun;
    }

    public void setMaxLatencyUnderrun(long maxLatencyUnderrun) {
        this.maxLatencyUnderrun = maxLatencyUnderrun;
    }

    public long getNoPlaybackTimeout() {
        return noPlaybackTimeout;
    }

    public void setNoPlaybackTimeout(long noPlaybackTimeout) {
        this.noPlaybackTimeout = noPlaybackTimeout;
    }

    public long getBrokenPlaybackTimeout() {
        return brokenPlaybackTimeout;
    }

    public void setBrokenPlaybackTimeout(long brokenPlaybackTimeout) {
        this.brokenPlaybackTimeout = brokenPlaybackTimeout;
    }

    public long getBreakageDetectionWindow() {
        return breakageDetectionWindow;
    }

    public void setBreakageDetectionWindow(long breakageDetectionWindow) {
        this.breakageDetectionWindow = breakageDetectionWindow;
    }
}
