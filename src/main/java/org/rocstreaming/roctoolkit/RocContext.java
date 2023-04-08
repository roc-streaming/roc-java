package org.rocstreaming.roctoolkit;

/**
 * Shared context.
 * <p>
 * Context contains memory pools and network worker threads, shared among objects attached
 * to the context. It is allowed both to create a separate context for every object, or
 * to create a single context shared between multiple objects.
 * <p>
 * <h3>Lifecycle</h3>
 * A context is created using {@link #RocContext() Context()} or
 * {@link #RocContext(RocContextConfig) Context(ContextConfig)} and destroyed using
 * {@link #close() close()}. <code>Context</code> class implements
 * {@link AutoCloseable AutoCloseable} so if it is used in a try-with-resources
 * statement the object is closed automatically at the end of the statement.
 * Objects can be attached and detached to an opened context at any moment from
 * any thread. However, the user should ensure that the context is not closed
 * until there are no objects attached to the context.
 *
 * @see Sender
 * @see Receiver
 */
public class RocContext extends NativeObject {

    /**
     * Validate context constructor parameters and open a new context if validation is successful.
     *
     * @param config                        should point to an initialized config.
     *
     * @return                              the native roc context pointer.
     *
     * @throws IllegalArgumentException     if the arguments are invalid.
     * @throws Exception                    if there are not enough resources.
     */
    private static long tryOpen(RocContextConfig config) throws IllegalArgumentException, Exception {
        if (config == null) throw new IllegalArgumentException();
        return open(config);
    }

    /**
     * Open a new context.
     * <p>
     * Allocates and initializes a new context. May start some background threads.
     *
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws Exception if there are not enough resources.
     */
    public RocContext() throws Exception {
        this(new RocContextConfig.Builder().build());
    }

    /**
     * Open a new context.
     * <p>
     * Allocates and initializes a new context. May start some background threads.
     *
     * @param config should point to an initialized config.
     *
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws Exception if there are not enough resources.
     */
    public RocContext(RocContextConfig config) throws IllegalArgumentException, Exception {
        super(tryOpen(config), null, RocContext::close);
    }

    private static native long open(RocContextConfig config) throws IllegalArgumentException, Exception;
    private static native void close(long nativePtr) throws Exception;
}
