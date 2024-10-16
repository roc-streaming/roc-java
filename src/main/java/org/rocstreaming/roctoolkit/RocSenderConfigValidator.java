package org.rocstreaming.roctoolkit;

/**
 * A <code>RocSenderConfigValidator</code> adds validation to RocSenderConfig builder.
 */
class RocSenderConfigValidator extends RocSenderConfig.Builder {
    @Override
    public RocSenderConfig build() {
        RocSenderConfig config = super.build();
        Check.notNull(config.getFrameEncoding(), "frameEncoding");
        Check.notNegative(config.getPacketLength(), "packetLength");
        Check.notNegative(config.getFecBlockSourcePackets(), "fecBlockSourcePackets");
        Check.notNegative(config.getFecBlockRepairPackets(), "fecBlockRepairPackets");
        return config;
    }
}
