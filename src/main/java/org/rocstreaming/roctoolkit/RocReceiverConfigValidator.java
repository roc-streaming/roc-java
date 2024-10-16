package org.rocstreaming.roctoolkit;

/**
 * A <code>RocReceiverConfigValidator</code> adds validation to RocReceiverConfig builder.
 */
class RocReceiverConfigValidator extends RocReceiverConfig.Builder {
    @Override
    public RocReceiverConfig build() {
        RocReceiverConfig config = super.build();
        Check.notNull(config.getFrameEncoding(), "frameEncoding");
        Check.notNegative(config.getTargetLatency(), "targetLatency");
        Check.notNegative(config.getLatencyTolerance(), "latencyTolerance");
        return config;
    }
}
