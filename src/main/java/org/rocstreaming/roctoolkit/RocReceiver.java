package org.rocstreaming.roctoolkit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Receiver node.
 * <p>
 * Receiver gets the network packets from multiple senders, decodes audio streams from them,
 * mixes multiple streams into a single stream, and returns it to the user.
 *
 *
 * <h2>Context</h2>
 * <p>
 * Receiver is automatically attached to a context when opened and detached from it when closed.
 * The user should not close the context until the receiver is closed.
 * <p>
 * Receiver work consists of two parts: packet reception and stream decoding. The decoding part
 * is performed in the receiver itself, and the reception part is performed in the context
 * network worker threads.
 *
 *
 * <h2>Life cycle</h2>
 * <p>
 * <ul>
 *   <li>A receiver is created using {@link RocReceiver()}.</li>
 *   <li>Optionally, the receiver parameters may be fine-tuned using
 *       {@link RocReceiver#configure()}.</li>
 *   <li>The receiver either binds local endpoints using {@link RocReceiver#bind()}, allowing
 *       senders connecting to them, or itself connects to remote sender endpoints using
 *       roc_receiver_connect(). What approach to use is up to the user.</li>
 *   <li>The audio stream is iteratively read from the receiver using {@link RocReceiver#read()}.
 *       Receiver returns the mixed stream from all connected senders.</li>
 *   <li>The receiver is destroyed using {@link RocReceiver#close()}.</li>
 * </ul>
 *
 *
 * <h2>Slots, interfaces, and endpoints</h2>
 * <p>
 * Receiver has one or multiple <b>slots</b>, which may be independently bound or connected.
 * Slots may be used to bind receiver to multiple addresses. Slots are numbered from zero and are
 * created automatically. In simple cases just use {@code ROC_SLOT_DEFAULT}.
 * <p>
 * Each slot has its own set of <em>interfaces</em>, one per each type defined in
 * {@link Interface}. The interface defines the type of the communication with the remote node
 * and the set of the protocols supported by it.
 * <p>
 * Supported actions with the interface:
 * <p>
 * <ul>
 *   <li>Call {@link RocReceiver#bind()} to bind the interface to a local {@link Endpoint}. In
 *       this case the receiver accepts connections from senders mixes their streams into the
 *       single output stream.</li>
 *   <li>Call roc_receiver_connect() to connect the interface to a remote {@link Endpoint}. In
 *       this case the receiver initiates connection to the sender and requests it to start
 *       sending media stream to the receiver.</li>
 * </ul>
 * <p>
 * Supported interface configurations:
 * <p>
 * <ul>
 *   <li>Bind {@link Interface#CONSOLIDATED} to a local endpoint (e.g. be an RTSP server).</li>
 *   <li>Connect {@link Interface#CONSOLIDATED} to a remote endpoint (e.g. be an RTSP
 *       client).</li>
 *   <li>Bind {@link Interface#AUDIO_SOURCE}, {@link Interface#AUDIO_REPAIR} (optionally, for
 *       FEC), and {@link Interface#AUDIO_CONTROL} (optionally, for control messages) to local
 *       endpoints (e.g. be an RTP/FECFRAME/RTCP receiver).</li>
 * </ul>
 * <p>
 * Slots can be removed using {@link RocReceiver#unlink()}. Removing a slot also removes all its
 * interfaces and terminates all associated connections.
 * <p>
 * Slots can be added and removed at any time on fly and from any thread. It is safe to do it
 * from another thread concurrently with reading frames. Operations with slots won't block
 * concurrent reads.
 *
 *
 * <h2>FEC scheme</h2>
 * <p>
 * If {@link Interface#CONSOLIDATED} is used, it automatically creates all necessary transport
 * interfaces and the user should not bother about them.
 * <p>
 * Otherwise, the user should manually configure {@link Interface#AUDIO_SOURCE} and
 * {@link Interface#AUDIO_REPAIR} interfaces:
 * <p>
 * <ul>
 *   <li>If FEC is disabled ( {@link FecEncoding#DISABLE} ), only {@link Interface#AUDIO_SOURCE}
 *       should be configured. It will be used to transmit audio packets.</li>
 *   <li>If FEC is enabled, both {@link Interface#AUDIO_SOURCE} and
 *       {@link Interface#AUDIO_REPAIR} interfaces should be configured. The second interface
 *       will be used to transmit redundant repair data.</li>
 * </ul>
 * <p>
 * The protocols for the two interfaces should correspond to each other and to the FEC scheme.
 * For example, if {@link FecEncoding#RS8M} is used, the protocols should be
 * {@link Protocol#RTP_RS8M_SOURCE} and {@link Protocol#RS8M_REPAIR}.
 *
 *
 * <h2>Sessions</h2>
 * <p>
 * Receiver creates a session object for every sender connected to it. Sessions can appear and
 * disappear at any time. Multiple sessions can be active at the same time.
 * <p>
 * A session is identified by the sender address. A session may contain multiple packet streams
 * sent to different receiver ports. If the sender employs FEC, the session will contain source
 * and repair packet streams. Otherwise, the session will contain a single source packet stream.
 * <p>
 * A session is created automatically on the reception of the first packet from a new address and
 * destroyed when there are no packets during a timeout. A session is also destroyed on other
 * events like a large latency underrun or overrun or broken playback, but if the sender
 * continues to send packets, it will be created again shortly.
 *
 *
 * <h2>Mixing</h2>
 * <p>
 * Receiver mixes audio streams from all currently active sessions into a single output stream.
 * <p>
 * The output stream continues no matter how much active sessions there are at the moment. In
 * particular, if there are no sessions, the receiver produces a stream with all zeros.
 * <p>
 * Sessions can be added and removed from the output stream at any time, probably in the middle
 * of a frame.
 *
 *
 * <h2>Sample rate</h2>
 * <p>
 * Every session may have a different sample rate. And even if nominally all of them are of the
 * same rate, device frequencies usually differ by a few tens of Hertz.
 * <p>
 * Receiver compensates these differences by adjusting the rate of every session stream to the
 * rate of the receiver output stream using a per-session resampler. The frequencies factor
 * between the sender and the receiver clocks is calculated dynamically for every session based
 * on the session incoming packet queue size.
 * <p>
 * Resampling is a quite time-consuming operation. The user can choose between several resampler
 * profiles providing different compromises between CPU consumption and quality.
 *
 *
 * <h2>Clock source</h2>
 * <p>
 * Receiver should decode samples at a constant rate that is configured when the receiver is
 * created. There are two ways to accomplish this:
 * <p>
 * <ul>
 *   <li>If the user enabled internal clock ( {@link ClockSource#INTERNAL} ), the receiver
 *       employs a CPU timer to block reads until it's time to decode the next bunch of samples
 *       according to the configured sample rate. This mode is useful when the user passes
 *       samples to a non-realtime destination, e.g. to an audio file.</li>
 *   <li>If the user enabled external clock ( {@link ClockSource#EXTERNAL} ), the samples read
 *       from the receiver are decoded immediately and hence the user is responsible to call read
 *       operation according to the sample rate. This mode is useful when the user passes samples
 *       to a realtime destination with its own clock, e.g. to an audio device. Internal clock
 *       should not be used in this case because the audio device and the CPU might have slightly
 *       different clocks, and the difference will eventually lead to an underrun or an
 *       overrun.</li>
 * </ul>
 *
 *
 * <h2>Thread safety</h2>
 * <p>
 * Can be used concurrently.
 *
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * RocReceiverConfig receiverConfig = RocReceiverConfig.builder()
 *         .frameEncoding(
 *                 MediaEncoding.builder()
 *                         .rate(44100)
 *                         .format(Format.PCM_FLOAT32)
 *                         .channels(ChannelLayout.STEREO)
 *                         .build()
 *         )
 *         .clockSource(ClockSource.INTERNAL)
           .build();
 * try (
 *     RocContext context = new RocContext();
 *     RocReceiver receiver = new RocReceiver(context, receiverConfig);
 * ) {
 *     receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:0"));
 *     receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:0"));
 *     float[] samples = new float[2];
 *     receiver.read(samples);
 * }
 * }
 * </pre>
 *
 *
 * @see RocContext
 * @see RocReceiverConfig
 * @see java.lang.AutoCloseable
 */
public class RocReceiver extends NativeObject {

    private static final Logger LOGGER = Logger.getLogger(RocReceiver.class.getName());

    private static long construct(RocContext context, RocReceiverConfig config) throws RocException {
        Check.notNull(context, "RocContext");
        Check.notNull(config, "RocReceiverConfig");

        try {
            LOGGER.log(Level.FINE, "entering RocReceiver(), contextPtr={0}, config={1}",
                    new Object[]{toHex(context.getPtr()), config});

            long ptr = nativeOpen(context.getPtr(), config);

            LOGGER.log(Level.FINE, "leaving RocReceiver(), ptr={0}", toHex(ptr));
            return ptr;
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "exception in RocReceiver(), exception={0}", exc);
            throw exc;
        }
    }

    private static void destroy(long ptr, RocContext context) {
        try {
            LOGGER.log(Level.FINE, "entering RocReceiver.close(), ptr={0}", toHex(ptr));

            nativeClose(ptr);

            LOGGER.log(Level.FINE, "leaving RocReceiver.close(), ptr={0}", toHex(ptr));
        } catch (RuntimeException exc) {
            LOGGER.log(Level.SEVERE, "exception in RocReceiver.close(), ptr={0}, exception={1}",
                    new Object[]{toHex(context.getPtr()), toHex(ptr), exc});
            throw exc;
        }
    }

    /**
     * Open a new receiver.
     * <p>
     * Allocates and initializes a new receiver, and attaches it to the context.
     *
     * @param context   should point to an opened context.
     * @param config    should point to an initialized config.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws RocException               if operation failed.
     */
    public RocReceiver(RocContext context, RocReceiverConfig config) throws RocException {
        super(construct(context, config), context, ptr -> destroy(ptr, context));
    }

    /**
     * Set receiver interface configuration.
     * <p>
     * Updates configuration of specified interface of specified slot. If
     * called, the call should be done before calling {@link RocReceiver#bind()}
     * or roc_receiver_connect() for the same interface.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     * <p>
     * If an error happens during configure, the whole slot is disabled and
     * marked broken. The slot index remains reserved. The user is responsible
     * for removing the slot using {@link RocReceiver#unlink()}, after which
     * slot index can be reused.
     *
     * @param slot     specifies the receiver slot.
     * @param iface    specifies the receiver interface.
     * @param config   specifies settings for the specified interface.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws RocException               if operation failed.
     */
    public void configure(Slot slot, Interface iface, InterfaceConfig config) throws RocException {
        Check.notNull(slot, "Slot");
        Check.notNull(iface, "Interface");
        Check.notNull(config, "InterfaceConfig");

        try {
            LOGGER.log(Level.FINE, "entering RocReceiver.configure(), ptr={0}, slot={1}, iface={2}, config={3}",
                    new Object[]{toHex(getPtr()), slot, iface, config});

            nativeConfigure(getPtr(), slot.getValue(), iface.value, config);

            LOGGER.log(Level.FINE, "leaving RocReceiver.configure(), ptr={0}", toHex(getPtr()));
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "exception in RocReceiver.configure(), ptr={0}, exception={1}",
                    new Object[]{toHex(getPtr()), exc});
            throw exc;
        }
    }

    /**
     * Bind the receiver interface to a local endpoint.
     * <p>
     * Checks that the endpoint is valid and supported by the interface,
     * allocates a new ingoing port, and binds it to the local endpoint.
     * <p>
     * Each slot's interface can be bound or connected only once. May be called
     * multiple times for different slots or interfaces.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     * <p>
     * If an error happens during bind, the whole slot is disabled and marked
     * broken. The slot index remains reserved. The user is responsible for
     * removing the slot using {@link RocReceiver#unlink()}, after which slot
     * index can be reused.
     * <p>
     * If {@code endpoint} has explicitly set zero port, the receiver is bound
     * to a randomly chosen ephemeral port. If the function succeeds, the actual
     * port to which the receiver was bound is written back to {@code endpoint}.
     *
     * @param slot      specifies the receiver slot.
     * @param iface     specifies the receiver interface.
     * @param endpoint  specifies the receiver endpoint.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws RocException               if operation failed.
     */
    public void bind(Slot slot, Interface iface, Endpoint endpoint) throws RocException {
        Check.notNull(slot, "Slot");
        Check.notNull(iface, "Interface");
        Check.notNull(endpoint, "Endpoint");

        try {
            LOGGER.log(Level.FINE, "entering RocReceiver.bind(), ptr={0}, slot={1}, iface={2}, endpoint={3}",
                    new Object[]{toHex(getPtr()), slot, iface, endpoint});

            nativeBind(getPtr(), slot.getValue(), iface.value, endpoint);

            LOGGER.log(Level.FINE, "leaving RocReceiver.bind(), ptr={0}", toHex(getPtr()));
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "exception in RocReceiver.bind(), ptr={0}, exception={1}",
                    new Object[]{toHex(getPtr()), exc});
            throw exc;
        }
    }

    /**
     * Delete receiver slot.
     * <p>
     * Disconnects, unbinds, and removes all slot interfaces and removes the
     * slot. All associated connections to remote nodes are properly terminated.
     * <p>
     * After unlinking the slot, it can be re-created again by re-using slot
     * index.
     *
     * @param slot   specifies the receiver slot to delete.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws RocException               if operation failed.
     */
    public void unlink(Slot slot) throws RocException {
        Check.notNull(slot, "Slot");

        try {
            LOGGER.log(Level.FINE, "entering RocReceiver.unlink(), ptr={0}, slot={1}",
                    new Object[]{toHex(getPtr()), slot});

            nativeUnlink(getPtr(), slot.getValue());

            LOGGER.log(Level.FINE, "leaving RocReceiver.unlink(), ptr={0}", toHex(getPtr()));
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "exception in RocReceiver.unlink(), ptr={0}, exception={1}",
                    new Object[]{toHex(getPtr()), exc});
            throw exc;
        }
    }

    /**
     * Read samples from the receiver.
     * <p>
     * Reads retrieved network packets, decodes packets, routes packets to
     * sessions, repairs losses, extracts samples, adjusts sample rate and
     * channel layout, compensates clock drift, mixes samples from all sessions,
     * and finally stores samples into the provided frame.
     * <p>
     * If {@link ClockSource#INTERNAL} is used, the function blocks until it's
     * time to decode the samples according to the configured sample rate.
     * <p>
     * Until the receiver is connected to at least one sender, it produces
     * silence. If the receiver is connected to multiple senders, it mixes their
     * streams into one.
     *
     * @param samples   should point to an initialized {@code float} array which will be
     *                  filled with samples.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws RocException               if operation failed.
     */
    public void read(float[] samples) throws RocException {
        Check.notNull(samples, "samples");

        nativeReadFloats(getPtr(), samples);
    }

    private static native long nativeOpen(long contextPtr, RocReceiverConfig config) throws RocException;
    private static native void nativeClose(long receiverPtr);

    private native void nativeConfigure(long receiverPtr, int slot, int iface, InterfaceConfig config) throws RocException;
    private native void nativeBind(long receiverPtr, int slot, int iface, Endpoint endpoint) throws RocException;
    private native void nativeUnlink(long receiverPtr, int slot) throws RocException;

    private native void nativeReadFloats(long receiverPtr, float[] samples) throws RocException;
}
