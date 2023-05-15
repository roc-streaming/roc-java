package org.rocstreaming.roctoolkit;

/**
 * Context configuration.
 * <p>
 * RocContextConfig object can be instantiated with {@link RocContextConfig.Builder RocContextConfig.Builder} objects.
 *
 * @see RocContext
 */
public class RocContextConfig {

    private int maxPacketSize;
    private int maxFrameSize;

    private RocContextConfig(int maxPacketSize, int maxFrameSize) {
        this.maxPacketSize = Check.notNegative(maxPacketSize, "maxPacketSize");
        this.maxFrameSize = Check.notNegative(maxFrameSize, "maxFrameSize");
    }

    /**
     *  Builder class for {@link RocContextConfig RocContextConfig} objects
     * @see RocContextConfig
     */
    public static class Builder {
        private int maxPacketSize;
        private int maxFrameSize;

        /**
         * Create a Builder object for building {@link RocContextConfig RocContextConfig}
         */
        public Builder() {
            this.maxPacketSize = 0;
            this.maxFrameSize = 0;
        }

        /**
         * @param maxPacketSize Maximum size in bytes of a network packet.
         *                      Defines the amount of bytes allocated per network packet.
         *                      Sender and receiver won't handle packets larger than this.
         *                      If zero, default value is used.
         * @return this Builder
         */
        public Builder maxPacketSize(int maxPacketSize) {
            this.maxPacketSize = maxPacketSize;
            return this;
        }

        /**
         * @param maxFrameSize Maximum size in bytes of an audio frame.
         *                     Defines the amount of bytes allocated per intermediate internal
         *                     frame in the pipeline. Does not limit the size of the frames
         *                     provided by user. If zero, default value is used.
         * @return this Builder
         */
        public Builder maxFrameSize(int maxFrameSize) {
            this.maxFrameSize = maxFrameSize;
            return this;
        }

        /**
         * Build the {@link RocContextConfig RocContextConfig} object with <code>Builder</code> parameters.
         * @return the new {@link RocContextConfig RocContextConfig}
         */
        public RocContextConfig build() {
            return new RocContextConfig(this.maxPacketSize, this.maxFrameSize);
        }
    }

    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    public void setMaxPacketSize(int maxPacketSize) {
        this.maxPacketSize = Check.notNegative(maxPacketSize, "maxPacketSize");
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = Check.notNegative(maxFrameSize, "maxFrameSize");;
    }
}
