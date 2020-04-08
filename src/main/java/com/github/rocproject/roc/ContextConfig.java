package com.github.rocproject.roc;

/**
 * Context configuration.
 * @see Context
 */
public class ContextConfig {

    private int maxPacketSize;
    private int maxFrameSize;

    /**
     * Create a new Context configuration object with default values
     */
    public ContextConfig() {
        this(0, 0);
    }

    /**
     * Create a new Context configuration object
     *
     * @param maxPacketSize Maximum size in bytes of a network packet.
     *                      Defines the amount of bytes allocated per network packet.
     *                      Sender and receiver won't handle packets larger than this.
     *                      If zero, default value is used.
     *
     * @param maxFrameSize  Maximum size in bytes of an audio frame.
     *                      Defines the amount of bytes allocated per intermediate internal
     *                      frame in the pipeline. Does not limit the size of the frames
     *                      provided by user.
     *                      If zero, default value is used.
     */
    public ContextConfig(int maxPacketSize, int maxFrameSize) {
        this.maxPacketSize = maxPacketSize;
        this.maxFrameSize = maxFrameSize;
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
