package org.rocstreaming.roctoolkit;

import java.io.IOException;

/**
 * Receiver peer.
 * <p>
 * Receiver gets the network packets from multiple senders, decodes audio streams
 * from them, mixes multiple streams into a single stream, and returns it to the user.
 *
 * <h3>Context</h3>
 * <p>
 * Receiver is automatically attached to a context when opened and detached from it when
 * closed. The user should not close the context until the receiver is closed.
 * <p>
 * Receiver work consists of two parts: packet reception and stream decoding. The
 * decoding part is performed in the receiver itself, and the reception part is
 * performed in the context network worker threads.
 *
 * <h3>Lifecycle</h3>
 * <ul>
 *     <li>A receiver is created using {@link RocReceiver#RocReceiver RocReceiver()}.</li>
 *     <li>Optionally, the receiver parameters may be fine-tuned using
 *     {@link RocReceiver#setMulticastGroup setMulticastGroup()} function.</li>
 *     <li>The receiver either binds local endpoints using {@link RocReceiver#bind bind()},
 *     allowing senders connecting to them, or itself connects to remote sender endpoints
 *     using {@link RocReceiver#connect connect()}. What approach to use is up to the user.</li>
 *     <li>The audio stream is iteratively read from the receiver using {@link RocReceiver#read read()}.
 *     Receiver returns the mixed stream from all connected senders.</li>
 *     <li>The receiver is destroyed using {@link RocReceiver#close close()}. <code>RocReceiver</code>
 *     class implements {@link AutoCloseable AutoCloseable} so if it is used in a try-with-resources
 *     statement the object is closed automatically at the end of the statement.</li>
 * </ul>
 *
 * <h3>Slots, interfaces, and endpoints</h3>
 * <p>
 * Receiver has one or multiple <b>slots</b>, which may be independently bound or connected.
 * Slots may be used to bind receiver to multiple addresses. Slots are numbered from
 * zero and are created automatically. In simple cases just use {@link Slot#DEFAULT}.
 * <p>
 * Each slot has its own set of <b>interfaces</b>, one per each type defined in {@link Interface}.
 * The interface defines the type of the communication with the remote peer
 * and the set of the protocols supported by it.
 * <p>
 * Supported actions with the interface:
 * <ul>
 *     <li>Call {@link RocReceiver#bind bind()} to bind the interface to a local {@link Endpoint}.
 *     In this case the receiver accepts connections from senders mixes their streams into the single output stream.</li>
 *     <li>Call {@link RocReceiver#connect connect()} to connect the interface to a remote {@link Endpoint}.
 *     In this case the receiver initiates connection to the sender and requests it to
 *     start sending media stream to the receiver.</li>
 * </ul>
 * <p>
 * Supported interface configurations:
 * <ul>
 *     <li>Bind {@link Interface#CONSOLIDATED} to a local endpoint (e.g. be an RTSP server).</li>
 *     <li>Connect {@link Interface#CONSOLIDATED} to a remote endpoint (e.g. be an RTSP client).</li>
 *     <li>Bind {@link Interface#AUDIO_SOURCE}, {@link Interface#AUDIO_REPAIR} (optionally, for FEC),
 *     and {@link Interface#AUDIO_CONTROL} (optionally, for control messages) to local endpoints
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
 * <h3>Sessions</h3>
 * Receiver creates a session object for every sender connected to it. Sessions can appear
 * and disappear at any time. Multiple sessions can be active at the same time.
 * <p>
 * A session is identified by the sender address. A session may contain multiple packet
 * streams sent to different receiver ports. If the sender employs FEC, the session will
 * contain source and repair packet streams. Otherwise, the session will contain a single
 * source packet stream.
 * <p>
 * A session is created automatically on the reception of the first packet from a new
 * address and destroyed when there are no packets during a timeout. A session is also
 * destroyed on other events like a large latency underrun or overrun or broken playback,
 * but if the sender continues to send packets, it will be created again shortly.
 *
 * <h3>Mixing</h3>
 * Receiver mixes audio streams from all currently active sessions into a single output
 * stream.
 * <p>
 * The output stream continues no matter how much active sessions there are at the moment.
 * In particular, if there are no sessions, the receiver produces a stream with all zeros.
 * <p>
 * Sessions can be added and removed from the output stream at any time, probably in the
 * middle of a frame.
 *
 * <h3>Sample rate</h3>
 * Every session may have a different sample rate. And even if nominally all of them are
 * of the same rate, device frequencies usually differ by a few tens of Hertz.
 * <p>
 * Receiver compensates these differences by adjusting the rate of every session stream to
 * the rate of the receiver output stream using a per-session resampler. The frequencies
 * factor between the sender and the receiver clocks is calculated dynamically for every
 * session based on the session incoming packet queue size.
 * <p>
 * Resampling is a quite time-consuming operation. The user can choose between completely
 * disabling resampling (at the cost of occasional underruns or overruns) or several
 * resampler profiles providing different compromises between CPU consumption and quality.
 *
 * <h3>Clock source</h3>
 * Receiver should decode samples at a constant rate that is configured when the receiver
 * is created. There are two ways to accomplish this:
 * <ul>
 *     <li>
 *         If the user enabled internal clock {@link ClockSource#INTERNAL},
 *         the receiver employs a CPU timer to block reads until it's time to
 *         decode the next bunch of samples according to the configured sample rate.
 * <p>
 *         This mode is useful when the user passes samples to a non-realtime destination, e.g. to an audio file.
 *     </li>
 *     <li>
 *         If the user enabled external clock {@link ClockSource#EXTERNAL},
 *         the samples read from the receiver are decoded immediately and hence the user is
 *         responsible to call read operation according to the sample rate.
 * <p>
 *         This mode is useful when the user passes samples to a realtime destination with its
 *         own clock, e.g. to an audio device. Internal clock should not be used in this case
 *         because the audio device and the CPU might have slightly different clocks, and the
 *         difference will eventually lead to an underrun or an overrun.
 *     </li>
 * </ul>
 *
 * <h3>Thread-safety</h3>
 * <p>
 * Can be used concurrently
 *
 * <h3>Example</h3>
 * <pre>
 * {@code
 * RocReceiverConfig config = RocReceiverConfig.builder()
 *             .frameSampleRate(SAMPLE_RATE)
 *             .frameChannels(ChannelSet.STEREO)
 *             .frameEncoding(FrameEncoding.PCM_FLOAT)
 *             .build();
 * try (
 *     RocContext context = new RocContext();
 *     RocReceiver receiver = new RocReceiver(context, config);
 * ) {
 *     receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:0"));
 *     receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:0"));
 *     float[] samples = new float[2];
 *     receiver.read(samples);
 * }
 * }
 * </pre>
 *
 * @see RocContext
 * @see RocReceiverConfig
 * @see java.lang.AutoCloseable
 */
public class RocReceiver extends NativeObject {

    /**
     * Validate receiver constructor parameters and open a new receiver if validation is successful.
     *
     * @param context should point to an opened context.
     * @param config  should point to an initialized config.
     * @return the native roc receiver pointer.
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws Exception                if an error occurred when creating the receiver.
     */
    private static long tryOpen(RocContext context, RocReceiverConfig config) throws IllegalArgumentException, Exception {
        Check.notNull(context, "context");
        Check.notNull(config, "config");
        return open(context.getPtr(), config);
    }

    /**
     * Open a new receiver.
     * <p>
     * Allocates and initializes a new receiver, and attaches it to the context.
     *
     * @param context should point to an opened context.
     * @param config  should point to an initialized config.
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws Exception                if an error occurred when creating the receiver.
     */
    public RocReceiver(RocContext context, RocReceiverConfig config) throws IllegalArgumentException, Exception {
        super(tryOpen(context, config), context, RocReceiver::close);
    }

    /**
     * Set receiver interface multicast group.
     * <p>
     * Optional.
     * <p>
     * Multicast group should be set only when binding receiver interface to an endpoint with
     * multicast IP address. If present, it defines an IP address of the OS network interface
     * on which to join the multicast group. If not present, no multicast group is joined.
     * <p>
     * It's possible to receive multicast traffic from only those OS network interfaces, on
     * which the process has joined the multicast group. When using multicast, the user should
     * either call this function, or join multicast group manually using OS-specific API.
     * <p>
     * It is allowed to set multicast group to `0.0.0.0` (for IPv4) or to `::` (for IPv6),
     * to be able to receive multicast traffic from all available interfaces. However, this
     * may not be desirable for security reasons.
     * <p>
     * Each slot's interface can have only one multicast group. The function should be called
     * before calling {@link RocReceiver#bind bind()} for the interface. It should not be called when
     * calling {@link RocReceiver#connect connect()} for the interface.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     *
     * @param slot  specifies the receiver slot
     * @param iface specifies the receiver interface
     * @param ip    should be IPv4 or IPv6 address
     * @throws IllegalArgumentException if the arguments are invalid.
     */
    public void setMulticastGroup(Slot slot, Interface iface, String ip) throws IllegalArgumentException, Exception {
        Check.notNull(slot, "slot");
        Check.notNull(iface, "iface");
        Check.notEmpty(ip, "ip");
        setMulticastGroup(getPtr(), slot.getValue(), iface.value, ip);
    }

    /**
     * Bind the receiver interface to a local endpoint.
     * <p>
     * Checks that the endpoint is valid and supported by the interface, allocates
     * a new ingoing port, and binds it to the local endpoint.
     * <p>
     * Each slot's interface can be bound or connected only once.
     * May be called multiple times for different slots or interfaces.
     * <p>
     * Automatically initializes slot with given index if it's used first time.
     * <p>
     * If <code>endpoint</code> has explicitly set zero port, the receiver is bound to a randomly
     * chosen ephemeral port. If the function succeeds, the actual port to which the
     * receiver was bound is written back to <code>endpoint</code>.
     *
     * @param slot     specifies the receiver slot
     * @param iface    specifies the receiver interface
     * @param endpoint specifies the receiver endpoint
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws IOException              if the address can't be bound or if there are not enough resources.
     */
    public void bind(Slot slot, Interface iface, Endpoint endpoint) throws IllegalArgumentException, IOException {
        Check.notNull(slot, "slot");
        Check.notNull(iface, "iface");
        Check.notNull(endpoint, "endpoint");
        bind(getPtr(), slot.getValue(), iface.value, endpoint);
    }

    public void connect(Endpoint endpoint) {
        throw new RuntimeException("connect not implemented");
    }

    /**
     * Read samples from the receiver.
     * <p>
     * Reads network packets received on bound ports, routes packets to sessions, repairs lost
     * packets, decodes samples, resamples and mixes them, and finally stores samples into the
     * provided frame.
     * <p>
     * If {@link ClockSource#INTERNAL} is used, the function blocks until it's time to decode the
     * samples according to the configured sample rate.
     * <p>
     * Until the receiver is connected to at least one sender, it produces silence.
     * If the receiver is connected to multiple senders, it mixes their streams into one.
     *
     * @param samples should point to an initialized <code>float</code> array which will be
     *                filled with samples.
     * @throws IllegalArgumentException if the arguments are invalid.
     * @throws IOException              if there are not enough resources.
     */
    public void read(float[] samples) throws IllegalArgumentException, IOException {
        Check.notNull(samples, "samples");
        readFloats(getPtr(), samples);
    }

    private static native long open(long contextPtr, RocReceiverConfig config) throws IllegalArgumentException, Exception;

    private native void setMulticastGroup(long receiverPtr, int slot, int iface, String ip) throws IllegalArgumentException;

    private native void bind(long receiverPtr, int slot, int iface, Endpoint endpoint) throws IllegalArgumentException, IOException;

    private native void readFloats(long receiverPtr, float[] samples) throws IOException;

    private static native void close(long receiverPtr) throws IOException;
}
