package org.rocstreaming.roctoolkit;

/**
 * A <code>RocReceiverConfigValidator</code> adds validation to RocReceiverConfig builder.
 */
class RocReceiverConfigValidator extends RocReceiverConfig.Builder {
    @Override
    public RocReceiverConfig build() {
        RocReceiverConfig config = super.build();
        Check.notNegative(config.getFrameSampleRate(), "frameSampleRate");
        Check.notNull(config.getFrameChannels(), "frameChannels");
        Check.notNull(config.getFrameEncoding(), "frameEncoding");
        Check.notNegative(config.getTargetLatency(), "targetLatency");
        Check.notNegative(config.getMaxLatencyOverrun(), "maxLatencyOverrun");
        Check.notNegative(config.getMaxLatencyUnderrun(), "maxLatencyUnderrun");
        Check.notNegative(config.getBreakageDetectionWindow(), "breakageDetectionWindow");
        return config;
    }
}
