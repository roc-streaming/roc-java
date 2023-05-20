package org.rocstreaming.roctoolkit;

import java.io.IOException;

/**
 * Sender peer.
 * <p>
 * Sender gets an audio stream from the user, encodes it into network packets, and
 * transmits them to a remote receiver.
 *
 * <h3>Context</h3>
 * <p>
 * Sender is automatically attached to a context when opened and detached from it when
 * closed. The user should not close the context until the sender is closed.
 * <p>
 * Sender work consists of two parts: stream encoding and packet transmission. The
 * encoding part is performed in the sender itself, and the transmission part is
 * performed in the context network worker threads.
 *
 * <h3>Lifecycle</h3>
 * <p>
 *     <ul>
 *         <li>A sender is created using {@link RocSender#RocSender RocSender()}.</li>
 *         <li>Optionally, the sender parameters may be fine-tuned using
 *         {@link RocSender#setOutgoingAddress(Slot, Interface, String)} function.</li>
 *         <li>The sender either binds local endpoints using {@link RocSender#bind bind()},
 *         allowing receivers connecting to them, or itself connects to remote receiver endpoints
 *         using {@link RocSender#connect connect()}. What approach to use is up to the user.</li>
 *         <li>The audio stream is iteratively written to the sender using {@link RocSender#write write()}.
 *         The sender encodes the stream into packets and send to connected receiver(s).</li>
 *         <li>The sender is destroyed using {@link RocSender#close close()}.</li>
 *     </ul>
 * <p>
 * <code>RocSender</code> class implements {@link AutoCloseable AutoCloseable} so if it is used in a
 * try-with-resources statement the object is closed automatically at the end of the statement.
 *
 * <h3>Slots, interfaces, and endpoints</h3>
 * <p>
 * Sender has one or multiple <b>slots</b>, which may be independently bound or connected.
 * Slots may be used to connect sender to multiple receivers. Slots are numbered from
 * zero and are created automatically. In simple cases just use {@link Slot#DEFAULT}.
 * <p>
 * Each slot has its own set of <b>interfaces</b>, one per each type defined in {@link Interface}.
 * The interface defines the type of the communication with the remote peer
 * and the set of the protocols supported by it.
 * <p>
 * Supported actions with the interface:
 * <ul>
 *     <li>Call {@link RocSender#bind bind()} to bind the interface to a local {@link Endpoint}.
 *     In this case the sender accepts connections from receivers and sends media stream
 *     to all connected receivers.</li>
 *     <li>Call {@link RocSender#connect(Slot, Interface, Endpoint)} to connect the interface
 *     to a remote {@link Endpoint}. In this case the sender initiates connection to the
 *     receiver and starts sending media stream to it.</li>
 * </ul>
 * <p>
 * Supported interface configurations:
 * <ul>
 *     <li>Connect {@link Interface#CONSOLIDATED} to a remote endpoint (e.g. be an RTSP client).</li>
 *     <li>Bind {@link Interface#CONSOLIDATED} to a local endpoint (e.g. be an RTSP server).</li>
 *     <li>Connect {@link Interface#AUDIO_SOURCE}, {@link Interface#AUDIO_REPAIR} (optionally, for FEC),
 *     and {@link Interface#AUDIO_CONTROL} (optionally, for control messages) to remote endpoints
 *     (e.g. be an RTP/FECFRAME/RTCP sender).</li>
 * </ul>
 *
 * <h3>FEC scheme</h3>
 * <p>
 * If {@link Interface#CONSOLIDATED} is used, it automatically creates all necessary
 * transport interfaces and the user should not bother about them.
 * <p>
 * Otherwise, the user should manually configure {@link Interface#AUDIO_SOURCE} and
 * {@link Interface#AUDIO_REPAIR} interfaces:
 * <ul>
 *     <li>If FEC is disabled {@link FecEncoding#DISABLE}, only {@link Interface#AUDIO_SOURCE}
 *     should be configured. It will be used to transmit audio packets.</li>
 *     <li>If FEC is enabled, both {@link Interface#AUDIO_SOURCE} and {@link Interface#AUDIO_REPAIR}
 *     interfaces should be configured. The second interface will be used to transmit redundant repair data.</li>
 * </ul>
 * <p>
 * The protocols for the two interfaces should correspond to each other and to the FEC
 * scheme. For example, if {@link FecEncoding#RS8M} is used, the protocols should be
 * {@link Protocol#RTP_RS8M_SOURCE} and {@link Protocol#RS8M_REPAIR}.
 *
 * <h3>Sample rate</h3>
 * <p>
 * If the sample rate of the user frames and the sample rate of the network packets are
 * different, the sender employs resampler to convert one rate to another.
 * <p>
 * Resampling is a quite time-consuming operation. The user can choose between completely
 * disabling resampling (and so use the same rate for frames and packets) or several
 * resampler profiles providing different compromises between CPU consumption and quality.
 *
 * <h3>Clock source</h3>
 * <p>
 * Sender should encode samples at a constant rate that is configured when the sender
 * is created. There are two ways to accomplish this:
 * <p>
 *     <ul>
 *         <li>
 *             If the user enabled internal clock {@link ClockSource#INTERNAL}, the sender employs a
 *             CPU timer to block writes until it's time to encode the next bunch of samples
 *             according to the configured sample rate.
 * <p>
 *             This mode is useful when the user gets samples from a non-realtime source, e.g.
 *             from an audio file.
 *         </li>
 *         <li>
 *             If the user enabled external clock {@link ClockSource#EXTERNAL}, the samples written to
 *             the sender are encoded and sent immediately, and hence the user is responsible to
 *             call write operation according to the sample rate.
 * <p>
 *             This mode is useful when the user gets samples from a realtime source with its own
 *             clock, e.g. from an audio device. Internal clock should not be used in this case
 *             because the audio device and the CPU might have slightly different clocks, and the
 *             difference will eventually lead to an underrun or an overrun.
 *         </li>
 *     </ul>
 *
 * <h3>Thread-safety</h3>
 * <p>
 * Can be used concurrently
 *
 * <h3>Example</h3>
 * <pre>
 * {@code
 * RocSenderConfig config = RocSenderConfig.builder()
 *             .frameSampleRate(SAMPLE_RATE)
 *             .frameChannels(ChannelSet.STEREO)
 *             .frameEncoding(FrameEncoding.PCM_FLOAT)
 *             .resamplerProfile(ResamplerProfile.DISABLE)
 *             .fecEncoding(FecEncoding.RS8M)
 *             .build();
 * try (
 *     RocContext context = new RocContext();
 *     RocSender sender = new RocSender(context, config);
 * ) {
 *     sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
 *     sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
 *     float[] samples = new float[] {2.0f, -2.0f};
 *     sender.write(samples);
 * }
 * }
 * </pre>
 *
 * @see RocContext
 * @see RocSenderConfig
 * @see java.lang.AutoCloseable
 */
public class RocSender extends NativeObject {

    /**
     * Validate sender constructor parameters and open a new sender if validation is successful.
     *
     * @param context should point to an opened context.
     * @param config  should point to an initialized config.
     * @return the native roc sender pointer.
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws Exception                if an error occurred when creating the sender.
     */
    private static long tryOpen(RocContext context, RocSenderConfig config) throws IllegalArgumentException, Exception {
        Check.notNull(context, "context");
        Check.notNull(config, "config");
        return open(context.getPtr(), config);
    }

    /**
     * Open a new sender.
     * Allocates and initializes a new sender, and attaches it to the context.
     *
     * @param context should point to an opened context.
     * @param config  should point to an initialized config.
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws Exception                if an error occurred when creating the sender.
     */
    public RocSender(RocContext context, RocSenderConfig config) throws IllegalArgumentException, Exception {
        super(tryOpen(context, config), context, RocSender::close);
    }

    /**
     * Set sender interface outgoing address.
     * <p>
     * Optional. Should be used only when connecting an interface to a remote endpoint.
     * <p>
     * If set, explicitly defines the IP address of the OS network interface from which to
     * send the outgoing packets. If not set, the outgoing interface is selected automatically
     * by the OS, depending on the remote endpoint address.
     * <p>
     * It is allowed to set outgoing address to `0.0.0.0` (for IPv4) or to `::` (for IPv6),
     * to achieve the same behavior as if it wasn't set, i.e. to let the OS to select the
     * outgoing interface automatically.
     * <p>
     * By default, the outgoing address is not set.
     * <p>
     * Each slot's interface can have only one outgoing address. The function should be called
     * before calling {@link RocSender#connect connect()} for this slot and interface. It should not be
     * called when calling {@link RocSender#bind bind()} for the interface.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     * <p>
     * **Parameters**
     *
     * @param slot  specifies the sender slot
     * @param iface specifies the sender interface
     * @param ip    should be IPv4 or IPv6 address
     * @throws Exception if an error occurred
     */
    public void setOutgoingAddress(Slot slot, Interface iface, String ip) throws Exception {
        Check.notNull(slot, "slot");
        Check.notNull(iface, "iface");
        Check.notEmpty(ip, "ip");
        setOutgoingAddress(getPtr(), slot.getValue(), iface.getValue(), ip);
    }

    public void bind(Endpoint endpoint) throws IllegalArgumentException, IOException {
        throw new RuntimeException("bind not implemented");
    }

    /**
     * Connect the sender interface to a remote receiver endpoint.
     * <p>
     * Checks that the endpoint is valid and supported by the interface, allocates
     * a new outgoing port, and connects it to the remote endpoint.
     * <p>
     * Each slot's interface can be bound or connected only once.
     * May be called multiple times for different slots or interfaces.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     *
     * @param slot     slot specifies the sender slot
     * @param iface    iface specifies the sender interface
     * @param endpoint endpoint specifies the receiver endpoint
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws IOException              if was error during connect
     */
    public void connect(Slot slot, Interface iface, Endpoint endpoint) throws IllegalArgumentException,
            IOException {
        Check.notNull(slot, "slot");
        Check.notNull(iface, "iface");
        Check.notNull(endpoint, "endpoint");
        connect(getPtr(), slot.getValue(), iface.getValue(), endpoint);
    }

    /**
     * Encode samples to packets and transmit them to the receiver.
     * <p>
     * Encodes samples to packets and enqueues them for transmission by the network worker
     * thread of the context.
     * <p>
     * If {@link ClockSource#INTERNAL} is used, the function blocks until it's time to transmit the
     * samples according to the configured sample rate. The function returns after encoding
     * and enqueuing the packets, without waiting when the packets are actually transmitted.
     * <p>
     * Until the sender is connected to at least one receiver, the stream is just dropped.
     * If the sender is connected to multiple receivers, the stream is duplicated to
     * each of them.
     *
     * @param samples array of samples to send.
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws IOException              if the sender if there are not enough resources.
     */
    public void write(float[] samples) throws IllegalArgumentException, IOException {
        Check.notNull(samples, "samples");
        writeFloats(getPtr(), samples);
    }

    private static native long open(long contextPtr, RocSenderConfig config) throws IllegalArgumentException, Exception;

    private native void setOutgoingAddress(long senderPtr, int slot, int iface, String ip) throws Exception;

    private native void connect(long senderPtr, int slot, int iface, Endpoint endpoint) throws IOException;

    private native void writeFloats(long senderPtr, float[] samples) throws IOException;

    private static native void close(long senderPtr) throws IOException;
}
