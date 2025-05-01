package org.rocstreaming.roctoolkit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared context.
 * <p>
 * Context contains memory pools and network worker threads, shared among objects attached to the
 * context. It is allowed both to create a separate context for every object, or to create a
 * single context shared between multiple objects.
 *
 *
 * <h2>Life cycle</h2>
 * <p>
 * A context is created using {@link RocContext()} and destroyed using
 * {@link RocContext#close()}. Objects can be attached and detached to an opened context at any
 * moment from any thread. However, the user should ensure that the context is not closed until
 * there are no objects attached to the context.
 *
 *
 * <h2>Thread safety</h2>
 * <p>
 * Can be used concurrently.
 *
 *
 * <h2>Auto closing</h2>
 * <p>
 * {@code RocContext} class implements {@link AutoCloseable}, so if it is used in a
 * try-with-resources statement, the object is closed automatically at the end of the statement.
 *
 *
 * @see {@link RocSender}
 * @see {@link RocReceiver}
 */
public class RocContext extends NativeObject {

    private static final Logger LOGGER = Logger.getLogger(RocContext.class.getName());

    private static long construct(RocContextConfig config) throws IllegalArgumentException, Exception {
        Check.notNull(config, "config");

        try {
            LOGGER.log(Level.FINE, "entering RocContext(), config={0}", config);

            long ptr = nativeOpen(config);

            LOGGER.log(Level.FINE, "leaving RocContext(), ptr={0}", toHex(ptr));
            return ptr;
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "exception in RocContext(), exception={0}", exc);
            throw exc;
        }
    }

    private static void destroy(long ptr) throws Exception {
        try {
            LOGGER.log(Level.FINE, "entering RocContext.close(), ptr={0}", toHex(ptr));

            nativeClose(ptr);

            LOGGER.log(Level.FINE, "leaving RocContext.close(), ptr={0}", toHex(ptr));
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "exception in RocContext.close(), ptr={0}, exception={1}",
                    new Object[]{toHex(ptr), exc});
            throw exc;
        }
    }

    /**
     * Open a new context.
     * <p>
     * Allocates and initializes a new context. May start some background threads.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws Exception                  if there are not enough resources.
     */
    public RocContext() throws IllegalArgumentException, Exception {
        this(RocContextConfig.builder().build());
    }

    /**
     * Open a new context.
     * <p>
     * Allocates and initializes a new context. May start some background threads.
     *
     * @param config   should point to an initialized config.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws Exception                  if there are not enough resources.
     */
    public RocContext(RocContextConfig config) throws IllegalArgumentException, Exception {
        super(construct(config), null, RocContext::destroy);
    }

    /**
     * Register custom encoding.
     * <p>
     * Registers {@code encoding} with given {@code encoding_id}. Registered encodings
     * complement built-in encodings defined by {@link PacketEncoding} enum. Whenever you
     * need to specify packet encoding, you can use both built-in and registered encodings.
     * <p>
     * On sender, you should register custom encoding and set to {@code packetEncoding} field
     * of {@link RocSenderConfig}, if you need to force specific encoding of packets, but
     * built-in set of encodings is not enough.
     * <p>
     * On receiver, you should register custom encoding with same id and specification, if
     * you did so on sender, and you're not using any signaling protocol (like RTSP) that is
     * capable of automatic exchange of encoding information.
     * <p>
     * In case of RTP, encoding id is mapped directly to payload type field (PT).
     *
     * @param encodingId   is numeric encoding identifier in range {@code [1; 127]}.
     * @param encoding     is encoding specification to be associated with this id.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     */
    public void registerEncoding(int encodingId, MediaEncoding encoding) throws IllegalArgumentException {
        Check.inRange(encodingId, 1, 127, "encodingId");
        Check.notNull(encoding, "encoding");

        try {
            LOGGER.log(Level.FINE, "entering RocContext.registerEncoding(), ptr={0}, encodingId={1}, encoding={2}",
                    new Object[]{toHex(getPtr()), encodingId, encoding});

            nativeRegisterEncoding(getPtr(), encodingId, encoding);

            LOGGER.log(Level.FINE, "leaving RocContext.registerEncoding(), ptr={0}", toHex(getPtr()));
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "exception in RocContext.registerEncoding(), ptr={0}, exception={1}",
                    new Object[]{toHex(getPtr()), exc});
            throw exc;
        }
    }

    private static native long nativeOpen(RocContextConfig config) throws IllegalArgumentException, Exception;
    private static native void nativeClose(long contextPtr) throws Exception;

    private static native void nativeRegisterEncoding(long contextPtr, int encodingId, MediaEncoding encoding) throws IllegalArgumentException;
}
