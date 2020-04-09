package com.github.rocproject.roc;

/**
 * Context configuration.
 *
 * ContextConfig object can be instantiated with {@link ContextConfig.Builder ContextConfig.Builder} objects.
 *
 * @see Context
 */
public class ContextConfig {
    static {
        RocLibrary.loadLibrary();
    }

    private int maxPacketSize;
    private int maxFrameSize;

    private ContextConfig(int maxPacketSize, int maxFrameSize) {
        this.maxPacketSize = maxPacketSize;
        this.maxFrameSize = maxFrameSize;
    }

    /**
     *  Builder class for {@link ContextConfig ContextConfig} objects
     * @see ContextConfig
     */
    public static class Builder {
        private int maxPacketSize;
        private int maxFrameSize;

        /**
         * Create a Builder object for building {@link ContextConfig ContextConfig}
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
         *                     provided by user.
         * @return this Builder
         */
        public Builder maxFrameSize(int maxFrameSize) {
            this.maxFrameSize = maxFrameSize;
            return this;
        }

        /**
         * Build the {@link ContextConfig ContextConfig} object with <code>Builder</code> parameters.
         * @return the new {@link ContextConfig ContextConfig}
         */
        public ContextConfig build() {
            return new ContextConfig(this.maxPacketSize, this.maxFrameSize);
        }
    }

    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    public void setMaxPacketSize(int maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }
}
