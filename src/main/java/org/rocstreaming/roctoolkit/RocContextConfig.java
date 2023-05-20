package org.rocstreaming.roctoolkit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * Context configuration.
 * <p>
 * RocContextConfig object can be instantiated with {@link RocContextConfig#builder()}.
 *
 * @see RocContext
 */
@Getter
@Builder(builderClassName = "ConfigBuilder", toBuilder = true, access = AccessLevel.PROTECTED)
public class RocContextConfig {

    /**
     * Maximum size in bytes of a network packet.
     * Defines the amount of bytes allocated per network packet.
     * Sender and receiver won't handle packets larger than this.
     * If zero, default value is used.
     */
    private int maxPacketSize;

    /**
     * Maximum size in bytes of an audio frame.
     * Defines the amount of bytes allocated per intermediate internal
     * frame in the pipeline. Does not limit the size of the frames
     * provided by user. If zero, default value is used.
     */
    private int maxFrameSize;

    public static ConfigBuilder builder() {
        return new ValidationBuilder();
    }

    private static class ValidationBuilder extends ConfigBuilder {
        @Override
        public RocContextConfig build() {
            Check.notNegative(super.maxPacketSize, "maxPacketSize");
            Check.notNegative(super.maxFrameSize, "maxFrameSize");
            return super.build();
        }
    }
}
