package org.rocstreaming.roctoolkit;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sender node.
 * <p>
 * Sender gets an audio stream from the user, encodes it into network packets, and transmits them
 * to a remote receiver.
 *
 *
 * <h2>Context</h2>
 * <p>
 * Sender is automatically attached to a context when opened and detached from it when closed.
 * The user should not close the context until the sender is closed.
 * <p>
 * Sender work consists of two parts: stream encoding and packet transmission. The encoding part
 * is performed in the sender itself, and the transmission part is performed in the context
 * network worker threads.
 *
 *
 * <h2>Life cycle</h2>
 * <p>
 * <ul>
 *   <li>A sender is created using {@link RocSender()}.</li>
 *   <li>Optionally, the sender parameters may be fine-tuned using
 *       {@link RocSender#configure()}.</li>
 *   <li>The sender either binds local endpoints using roc_sender_bind(), allowing receivers
 *       connecting to them, or itself connects to remote receiver endpoints using
 *       {@link RocSender#connect()}. What approach to use is up to the user.</li>
 *   <li>The audio stream is iteratively written to the sender using {@link RocSender#write()}.
 *       The sender encodes the stream into packets and send to connected receiver(s).</li>
 *   <li>The sender is destroyed using {@link RocSender#close()}.</li>
 * </ul>
 *
 *
 * <h2>Slots, interfaces, and endpoints</h2>
 * <p>
 * Sender has one or multiple <b>slots</b>, which may be independently bound or connected. Slots
 * may be used to connect sender to multiple receivers. Slots are numbered from zero and are
 * created automatically. In simple cases just use {@code ROC_SLOT_DEFAULT}.
 * <p>
 * Each slot has its own set of <em>interfaces</em>, one per each type defined in
 * {@link Interface}. The interface defines the type of the communication with the remote node
 * and the set of the protocols supported by it.
 * <p>
 * Supported actions with the interface:
 * <p>
 * <ul>
 *   <li>Call roc_sender_bind() to bind the interface to a local {@link Endpoint}. In this case
 *       the sender accepts connections from receivers and sends media stream to all connected
 *       receivers.</li>
 *   <li>Call {@link RocSender#connect()} to connect the interface to a remote {@link Endpoint}.
 *       In this case the sender initiates connection to the receiver and starts sending media
 *       stream to it.</li>
 * </ul>
 * <p>
 * Supported interface configurations:
 * <p>
 * <ul>
 *   <li>Connect {@link Interface#CONSOLIDATED} to a remote endpoint (e.g. be an RTSP
 *       client).</li>
 *   <li>Bind {@link Interface#CONSOLIDATED} to a local endpoint (e.g. be an RTSP server).</li>
 *   <li>Connect {@link Interface#AUDIO_SOURCE}, {@link Interface#AUDIO_REPAIR} (optionally, for
 *       FEC), and {@link Interface#AUDIO_CONTROL} (optionally, for control messages) to remote
 *       endpoints (e.g. be an RTP/FECFRAME/RTCP sender).</li>
 * </ul>
 * <p>
 * Slots can be removed using {@link RocSender#unlink()}. Removing a slot also removes all its
 * interfaces and terminates all associated connections.
 * <p>
 * Slots can be added and removed at any time on fly and from any thread. It is safe to do it
 * from another thread concurrently with writing frames. Operations with slots won't block
 * concurrent writes.
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
 * <h2>Sample rate</h2>
 * <p>
 * If the sample rate of the user frames and the sample rate of the network packets are
 * different, the sender employs resampler to convert one rate to another.
 * <p>
 * Resampling is a quite time-consuming operation. The user can choose between several resampler
 * profiles providing different compromises between CPU consumption and quality.
 *
 *
 * <h2>Clock source</h2>
 * <p>
 * Sender should encode samples at a constant rate that is configured when the sender is created.
 * There are two ways to accomplish this:
 * <p>
 * <ul>
 *   <li>If the user enabled internal clock ( {@link ClockSource#INTERNAL} ), the sender employs
 *       a CPU timer to block writes until it's time to encode the next bunch of samples
 *       according to the configured sample rate. This mode is useful when the user gets samples
 *       from a non-realtime source, e.g. from an audio file.</li>
 *   <li>If the user enabled external clock ( {@link ClockSource#EXTERNAL} ), the samples written
 *       to the sender are encoded and sent immediately, and hence the user is responsible to
 *       call write operation according to the sample rate. This mode is useful when the user
 *       gets samples from a realtime source with its own clock, e.g. from an audio device.
 *       Internal clock should not be used in this case because the audio device and the CPU
 *       might have slightly different clocks, and the difference will eventually lead to an
 *       underrun or an overrun.</li>
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
 * RocSenderConfig senderConfig = RocSenderConfig.builder()
 *         .frameEncoding(
 *                 MediaEncoding.builder()
 *                         .rate(44100)
 *                         .format(Format.PCM_FLOAT32)
 *                         .channels(ChannelLayout.STEREO)
 *                         .build()
 *         )
 *         .fecEncoding(FecEncoding.RS8M)
 *         .clockSource(ClockSource.INTERNAL)
 *         .build();
 * try (
 *     RocContext context = new RocContext();
 *     RocSender sender = new RocSender(context, senderConfig);
 * ) {
 *     sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
 *     sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
 *     float[] samples = new float[] {2.0f, -2.0f};
 *     sender.write(samples);
 * }
 * }
 * </pre>
 *
 *
 * @see RocContext
 * @see RocSenderConfig
 * @see java.lang.AutoCloseable
 */
public class RocSender extends NativeObject {

    private static final Logger LOGGER = Logger.getLogger(RocSender.class.getName());

    private static long construct(RocContext context, RocSenderConfig config) throws IllegalArgumentException, Exception {
        Check.notNull(context, "context");
        Check.notNull(config, "config");

        LOGGER.log(Level.FINE, "starting RocSender.open(), context ptr={0}, config={1}",
                new Object[]{toHex(context.getPtr()), config});
        long ptr = nativeOpen(context.getPtr(), config);
        LOGGER.log(Level.FINE, "finished RocSender.open(), context ptr={0}, ptr={1}",
                new Object[]{toHex(context.getPtr()), toHex(ptr)});
        return ptr;
    }

    private static void destroy(long ptr, RocContext context) throws Exception {
        LOGGER.log(Level.FINE, "starting RocSender.close(), context ptr={0}, ptr={1}",
                new Object[]{toHex(context.getPtr()), toHex(ptr)});
        nativeClose(ptr);
        LOGGER.log(Level.FINE, "finished RocSender.close(), context ptr={0}, ptr={1}",
                new Object[]{toHex(context.getPtr()), toHex(ptr)});
    }

    /**
     * Open a new sender.
     * Allocates and initializes a new sender, and attaches it to the context.
     *
     * @param context   should point to an opened context.
     * @param config    should point to an initialized config.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws Exception                  if an error occurred when creating the sender.
     */
    public RocSender(RocContext context, RocSenderConfig config) throws IllegalArgumentException, Exception {
        super(construct(context, config), context, ptr -> destroy(ptr, context));
    }

    /**
     * Set sender interface configuration.
     * <p>
     * Updates configuration of specified interface of specified slot. If
     * called, the call should be done before calling roc_sender_bind() or
     * {@link RocSender#connect()} for the same interface.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     * <p>
     * If an error happens during configure, the whole slot is disabled and
     * marked broken. The slot index remains reserved. The user is responsible
     * for removing the slot using {@link RocSender#unlink()}, after which slot
     * index can be reused.
     *
     * @param slot     slot specifies the sender slot.
     * @param iface    iface specifies the sender interface.
     * @param config   settings for the specified interface.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     */
    public void configure(Slot slot, Interface iface, InterfaceConfig config) throws IllegalArgumentException {
        Check.notNull(slot, "slot");
        Check.notNull(iface, "iface");
        Check.notNull(config, "config");

        LOGGER.log(Level.FINE, "starting RocSender.configure(), ptr={0}, slot={1}, iface={2}, config={3}",
                new Object[]{toHex(getPtr()), slot, iface, config});
        nativeConfigure(getPtr(), slot.getValue(), iface.value, config);
        LOGGER.log(Level.FINE, "finished RocSender.configure(), ptr={0}", new Object[]{toHex(getPtr())});
    }

    /**
     * Connect the sender interface to a remote receiver endpoint.
     * <p>
     * Checks that the endpoint is valid and supported by the interface,
     * allocates a new outgoing port, and connects it to the remote endpoint.
     * <p>
     * Each slot's interface can be bound or connected only once. May be called
     * multiple times for different slots or interfaces.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     * <p>
     * If an error happens during connect, the whole slot is disabled and marked
     * broken. The slot index remains reserved. The user is responsible for
     * removing the slot using {@link RocSender#unlink()}, after which slot
     * index can be reused.
     *
     * @param slot       slot specifies the sender slot.
     * @param iface      iface specifies the sender interface.
     * @param endpoint   endpoint specifies the receiver endpoint.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws IOException                if was error during connect.
     */
    public void connect(Slot slot, Interface iface, Endpoint endpoint) throws IllegalArgumentException, IOException {
        Check.notNull(slot, "slot");
        Check.notNull(iface, "iface");
        Check.notNull(endpoint, "endpoint");

        LOGGER.log(Level.FINE, "starting RocSender.connect(), ptr={0}, slot={1}, iface={2}, endpoint={3}",
                new Object[]{toHex(getPtr()), slot, iface, endpoint});
        nativeConnect(getPtr(), slot.getValue(), iface.value, endpoint);
        LOGGER.log(Level.FINE, "finished RocSender.connect(), ptr={0}", new Object[]{toHex(getPtr())});
    }

    /**
     * Delete sender slot.
     * <p>
     * Disconnects, unbinds, and removes all slot interfaces and removes the
     * slot. All associated connections to remote nodes are properly terminated.
     * <p>
     * After unlinking the slot, it can be re-created again by re-using slot
     * index.
     *
     * @param slot   specifies the sender slot to delete.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     */
    public void unlink(Slot slot) throws IllegalArgumentException {
        Check.notNull(slot, "slot");

        LOGGER.log(Level.FINE, "starting RocSender.unlink(), ptr={0}, slot={1}", new Object[]{toHex(getPtr()), slot});
        nativeUnlink(getPtr(), slot.getValue());
        LOGGER.log(Level.FINE, "finished RocSender.connect(), ptr={0}", new Object[]{toHex(getPtr())});
    }

    /**
     * Encode samples to packets and transmit them to the receiver.
     * <p>
     * Encodes samples to packets and enqueues them for transmission by the
     * network worker thread of the context.
     * <p>
     * If {@link ClockSource#INTERNAL} is used, the function blocks until it's
     * time to transmit the samples according to the configured sample rate. The
     * function returns after encoding and enqueuing the packets, without
     * waiting when the packets are actually transmitted.
     * <p>
     * Until the sender is connected to at least one receiver, the stream is
     * just dropped. If the sender is connected to multiple receivers, the
     * stream is duplicated to each of them.
     *
     * @param samples   array of samples to send.
     *
     * @throws IllegalArgumentException   if the arguments are invalid.
     * @throws IOException                if the sender if there are not enough resources.
     */
    public void write(float[] samples) throws IllegalArgumentException, IOException {
        Check.notNull(samples, "samples");
        nativeWriteFloats(getPtr(), samples);
    }

    private static native long nativeOpen(long contextPtr, RocSenderConfig config) throws IllegalArgumentException, Exception;
    private static native void nativeClose(long senderPtr) throws IOException;

    private native void nativeConfigure(long senderPtr, int slot, int iface, InterfaceConfig config) throws IllegalArgumentException;
    private native void nativeConnect(long senderPtr, int slot, int iface, Endpoint endpoint) throws IllegalArgumentException, IOException;
    private native void nativeUnlink(long senderPtr, int slot) throws IllegalArgumentException;

    private native void nativeWriteFloats(long senderPtr, float[] samples) throws IOException;
}
