package org.rocstreaming.roctoolkit;

/**
 * Adds validation to {@link RocContextConfig} builder.
 */
class RocContextConfigValidator extends RocContextConfig.Builder {
    @Override
    public RocContextConfig build() {
        RocContextConfig config = super.build();

        Check.notNegative(config.getMaxPacketSize(), "RocContextConfig.maxPacketSize");
        Check.notNegative(config.getMaxFrameSize(), "RocContextConfig.maxFrameSize");

        return config;
    }
}
