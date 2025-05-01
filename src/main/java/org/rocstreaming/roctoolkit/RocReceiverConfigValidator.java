package org.rocstreaming.roctoolkit;

/**
 * Adds validation to {@link RocReceiverConfig} builder.
 */
class RocReceiverConfigValidator extends RocReceiverConfig.Builder {
    @Override
    public RocReceiverConfig build() {
        RocReceiverConfig config = super.build();

        Check.notNull(config.getFrameEncoding(), "RocReceiverConfig.frameEncoding");
        Check.notNegative(config.getTargetLatency(), "RocReceiverConfig.targetLatency");
        Check.notNegative(config.getLatencyTolerance(), "RocReceiverConfig.latencyTolerance");

        return config;
    }
}
