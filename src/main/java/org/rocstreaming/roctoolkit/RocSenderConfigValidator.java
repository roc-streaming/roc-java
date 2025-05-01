package org.rocstreaming.roctoolkit;

/**
 * Adds validation to {@link RocSenderConfig} builder.
 */
class RocSenderConfigValidator extends RocSenderConfig.Builder {
    @Override
    public RocSenderConfig build() {
        RocSenderConfig config = super.build();

        Check.notNull(config.getFrameEncoding(), "RocSenderConfig.frameEncoding");
        Check.notNegative(config.getPacketLength(), "RocSenderConfig.packetLength");
        Check.notNegative(config.getFecBlockSourcePackets(), "RocSenderConfig.fecBlockSourcePackets");
        Check.notNegative(config.getFecBlockRepairPackets(), "RocSenderConfig.fecBlockRepairPackets");

        return config;
    }
}
