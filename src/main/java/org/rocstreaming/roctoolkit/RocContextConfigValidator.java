package org.rocstreaming.roctoolkit;


/**
 * A <code>RocContextConfigValidator</code> adds validation to RocContextConfig builder.
 */
class RocContextConfigValidator extends RocContextConfig.Builder {
    @Override
    public RocContextConfig build() {
        RocContextConfig config = super.build();
        Check.notNegative(config.getMaxPacketSize(), "maxPacketSize");
        Check.notNegative(config.getMaxFrameSize(), "maxFrameSize");
        return config;
    }
}
