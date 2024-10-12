package org.rocstreaming.roctoolkit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared context.
 * <p>
 * Context contains memory pools and network worker threads, shared among objects attached
 * to the context. It is allowed both to create a separate context for every object, or
 * to create a single context shared between multiple objects.
 * <p>
 * <h2>Lifecycle</h2>
 * A context is created using {@link #RocContext() RocContext()} or
 * {@link #RocContext(RocContextConfig) RocContext(RocContextConfig)} and destroyed using
 * {@link #close() close()}. <code>RocContext</code> class implements
 * {@link AutoCloseable AutoCloseable} so if it is used in a try-with-resources
 * statement the object is closed automatically at the end of the statement.
 * Objects can be attached and detached to an opened context at any moment from
 * any thread. However, the user should ensure that the context is not closed
 * until there are no objects attached to the context.
 *
 * @see RocSender
 * @see RocReceiver
 */
public class RocContext extends NativeObject {

    private static final Logger LOGGER = Logger.getLogger(RocContext.class.getName());

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
    private static long construct(RocContextConfig config) throws IllegalArgumentException, Exception {
        Check.notNull(config, "config");
        LOGGER.log(Level.FINE, "starting RocContext.open(), config={0}", new Object[]{config});
        long ptr = open(config);
        LOGGER.log(Level.FINE, "finished RocContext.open(), ptr={0}", new Object[]{toHex(ptr)});
        return ptr;
    }

    /**
     * Destruct native object
     */
    private static void destroy(long ptr) throws Exception {
        LOGGER.log(Level.FINE, "starting RocContext.close(), ptr={0}", new Object[]{toHex(ptr)});
        close(ptr);
        LOGGER.log(Level.FINE, "finished RocContext.close(), ptr={0}", new Object[]{toHex(ptr)});
    }

    /**
     * Open a new context.
     * <p>
     * Allocates and initializes a new context. May start some background threads.
     *
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws Exception if there are not enough resources.
     */
    public RocContext() throws IllegalArgumentException, Exception {
        this(RocContextConfig.builder().build());
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
        super(construct(config), null, RocContext::destroy);
    }

    private static native long open(RocContextConfig config) throws IllegalArgumentException, Exception;
    private static native void close(long nativePtr) throws Exception;
}
