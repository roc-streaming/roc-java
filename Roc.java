public class Roc {

    public class RocPortType {
        public static final int ROC_PORT_AUDIO_SOURCE = 1;
        public static final int ROC_PORT_AUDIO_REPAIR = 2;
    }

    public class RocProtocol {
        public static final int ROC_PROTO_RTP = 15;
        public static final int ROC_PROTO_RTP_RS8M_SOURCE = 25;
        public static final int ROC_PROTO_RS8M_REPAIR = 35;
        public static final int ROC_PROTO_RTP_LDPC_SOURCE = 45;
        public static final int ROC_PROTO_LDPC_REPAIR = 5;
    }

    public class RocFECCode {
        public static final int ROC_FEC_DISABLE = -1;
        public static final int ROC_FEC_DEFAULT = 0;
        public static final int ROC_FEC_RS8M = 1;
        public static final int ROC_FEC_LDPC_STAIRCASE = 2;
    }

    public class RocPacketEncoding {
        public static final int ROC_PACKET_ENCODING_AVP_L16 = 2;
    }

    public class RocFrameEncoding {
        public static final int ROC_FRAME_ENCODING_PCM_FLOAT = 1;
    }

    public class RocChannelSet {
        public static final int ROC_CHANNEL_SET_STEREO = 2;
    }

    public class RocResamplerProfile {
        public static final int ROC_RESAMPLER_DISABLE = -1;
        public static final int ROC_RESAMPLER_DEFAULT = 0;
        public static final int ROC_RESAMPLER_HIGH = 1;
        public static final int ROC_RESAMPLER_MEDIUM = 2;
        public static final int ROC_RESAMPLER_LOW = 3;
    }

    public class ContextConfig {
        int maxPacketSize;
        int maxFrameSize;

        public ContextConfig(int maxPacketSize, int maxFrameSize) {
            this.maxPacketSize = maxPacketSize;
            this.maxFrameSize = maxFrameSize;
        }

        public int getMaxPacketSize() {
            return maxPacketSize;
        }

        public int getMaxFrameSize() {
            return maxFrameSize;
        }
    }

    public class RocSenderConfig {
        int frameSampleRate;
        int frameChannels;
        int frameEncoding;
        int packetSampleRate;
        int packetChannels;
        int packetEncoding;
        long packetLength;
        int packetInterleaving;
        int automaticTiming;
        int resamplerProfile;
        int fecCode;
        int fecBlockSourcePackets;
        int fecBlockRepairPackets;

        public RocSenderConfig(int frameSampleRate, int frameChannels,
                int frameEncoding, int packetSampleRate,
                int packetChannels, int packetEncoding, long packetLength,
                int packetInterleaving, int automaticTiming,
                int resamplerProfile, int fecCode,
                int fecBlockSourcePackets, int fecBlockRepairPackets) {

            this.frameSampleRate = frameSampleRate;
            this.frameChannels = frameChannels;
            this.frameEncoding = frameEncoding;
            this.packetSampleRate = packetSampleRate;
            this.packetChannels = packetChannels;
            this.packetEncoding = packetEncoding;
            this.packetLength = packetLength;
            this.packetInterleaving = packetInterleaving;
            this.automaticTiming = automaticTiming;
            this.resamplerProfile = resamplerProfile;
            this.fecCode = fecCode;
            this.fecBlockSourcePackets = fecBlockSourcePackets;
            this.fecBlockRepairPackets = fecBlockRepairPackets;
        }
    }

    public class RocContext {
        public long context; /* pointer to struct roc_context */

        public RocContext() {
            context = rocContextOpen(context);
        }

        public void close() {
            rocContextClose(context);
        }

        private native long rocContextOpen(long context);

        private native void rocContextClose(long context);

    }

    public class RocSender {
        public long senderPointer; /* pointer to struct roc_sender */
        public long contextPointer; /* poninter to struct roc_context */
        private RocSenderConfig senderConfig;


        public RocSender(long contextPointer, RocSenderConfig senderConfig) {
            this.contextPointer = contextPointer;
            this.senderConfig = senderConfig;
            senderPointer = rocSenderOpen(contextPointer, senderConfig);
        }

        public int close() {
            return rocSenderClose(senderPointer);
        }

        public int bind(RocAddress address) {
            return rocSenderBind(senderPointer, address.pointer);
        }

        public int connect(int rocPortType,
                           int rocProtocol,
                           RocAddress address)
        {
            return rocSenderConnect(senderPointer,
                                    rocPortType,
                                    rocProtocol,
                                    address.pointer);
        }

        public int write(RocFrame frame) {
            return rocSenderWrite(senderPointer, frame.samplesPointer);
        }

        private native long rocSenderOpen(long contextPointer,
                                          RocSenderConfig config);

        private native int rocSenderClose(long senderPointer);

        private native int rocSenderBind(long senderPointer,
                                         long addressPointer);

        private native int rocSenderConnect(long senderPointer,
                                            int rocPortType,
                                            int rocProtocol,
                                            long addressPointer);

        private native int rocSenderWrite(long senderPointer,
                                          long framePointer);

    }

    public class RocReceiver {
        public long contextPointer; /* points to struct roc_context */
        public long receiverPointer; /* points to struct roc_receiver */
        public RocReceiverConfig receiverConfig;

        public RocReceiver(long contextPointer,
                           RocReceiverConfig receiverConfig) {
            this.contextPointer = contextPointer;
            this.receiverConfig = receiverConfig;
            receiverPointer = rocReceiverOpen(contextPointer,
                                              receiverConfig);
        }

        public int close() {
            return rocReceiverClose(receiverPointer);
        }

        public int bind(RocAddress address) {
            return rocReceiverBind(receiverPointer, address.pointer);
        }

        public int connect(int rocPortType,
                           int rocProtocol,
                           RocAddress address)
        {
            return rocReceiverConnect(receiverPointer,
                                      rocPortType,
                                      rocProtocol,
                                      address.pointer);
        }

        public int write(RocFrame frame) {
            return rocReceiverWrite(receiverPointer, frame.samplesPointer);
        }
        private native long rocReceiverOpen(long contextPointer,
                                            RocReceiverConfig config);

        private native int rocReceiverClose(long receiverPointer);

        private native int rocReceiverBind(long receiverPointer,
                                           long addressPointer);

        private native int rocReceiverConnect(long receiverPointer,
                                              int rocPortType,
                                              int rocProtocol,
                                              long addressPointer);

        private native int rocReceiverWrite(long receiverPointer,
                                            long framePointer);
    }

    public class RocFamily {
        public static final int ROC_AF_INVALID = -1;
        public static final int ROC_AF_AUTO = 0;
        public static final int ROC_AF_IPv4 = 1;
        public static final int ROC_AF_IPv6 = 2;
    }

    public class RocAddress {
        public long pointer; /* pointer to struct roc_address */

        public RocAddress(int family, String ip, int port) {
            pointer = RocAddressInit(pointer, family, ip, port);
        }


        public int getAddressFamily() {
            return rocAddressFamily(pointer);
        }
        public String getAddressIP() {
            return rocAddressIP(pointer);
        }

        public int getAddressPort() {
            return rocAddressPort(pointer);
        }

        private native int rocAddressFamily(long address);

        private native String rocAddressIP(long address);

        private native int rocAddressPort(long address);

        private native long RocAddressInit(long address, int family,
                                           String ip, int port);
    }

    public class RocFrame {
        public long framePointer; /* pointer to struct roc_frame */
        public long samplesPointer; /* pointer to void */
        public int samplesSize;

        public float[] samples;

        public RocFrame(float samples[]) {
            this.samples = samples;
            framePointer = rocCreateFrame(this.samples);
            samplesSize = samples.length;
            samplesPointer = getSamplesPointer(this.framePointer);
        }

        private native long getSamplesPointer(long framePointer);

        private native long rocCreateFrame(float samples[]);
    }

    public class ReceiverCommonConfig {
        public int outputSampleRate;
        public long outputChannels; /* uint_32 */
        public long internalFrameSize;
        public boolean resampling;
        public boolean timing;
        public boolean poisoning;
        public boolean beeping;

        public ReceiverCommonConfig(int outputSampleRate,
                                    long outputChannels,
                                    long internalFrameSize,
                                    boolean resampling,
                                    boolean timing, boolean poisoning,
                                    boolean beeping)
        {
            this.outputSampleRate = outputSampleRate;
            this.outputChannels = outputChannels;
            this.internalFrameSize = internalFrameSize;
            this.resampling = resampling;
            this.timing = timing;
            this.poisoning = poisoning;
            this.beeping = beeping;
        }


        public ReceiverCommonConfig(long outputChannels,
                                    long internalFrameSize,
                                    boolean resampling,
                                    boolean timing, boolean poisoning,
                                    boolean beeping)
        {
            this.outputSampleRate = new RocDefaults().defaultSampleRate();
            this.outputChannels = outputChannels;
            this.internalFrameSize = internalFrameSize;
            this.resampling = resampling;
            this.timing = timing;
            this.poisoning = poisoning;
            this.beeping = beeping;
        }

        public ReceiverCommonConfig(long internalFrameSize,
                                    boolean resampling,
                                    boolean timing, boolean poisoning,
                                    boolean beeping,
                                    int outputSampleRate)
        {
            this.outputSampleRate = outputSampleRate;
            this.outputChannels = new RocDefaults().defaultChannelMask();
            this.internalFrameSize = internalFrameSize;
            this.resampling = resampling;
            this.timing = timing;
            this.poisoning = poisoning;
            this.beeping = beeping;
        }

        public ReceiverCommonConfig(long internalFrameSize,
                                    boolean resampling,
                                    boolean timing, boolean poisoning,
                                    boolean beeping)
        {
            RocDefaults defaults = new RocDefaults();
            this.outputSampleRate = defaults.defaultSampleRate();
            this.outputChannels = defaults.defaultChannelMask();
            this.internalFrameSize = internalFrameSize;
            this.resampling = resampling;
            this.timing = timing;
            this.poisoning = poisoning;
            this.beeping = beeping;
        }
    }
        public class RocDefaults {
            public native int defaultSampleRate();

            public native long defaultChannelMask();

            public native long defaultPacketLength();

            public native long defaultLatency();

            public native long defaultInternalFrameSize();

            public native int defaultMinLatencyFactor();

            public native int defaultMaxLatencyFactor();

        }

    public class ReceiverSessionConfig {
        public long targetLatency; /* ns */
        public long channels; /* channel mask */
        public long payloadType; /* uint32 */
        public FecReaderConfig fecReaderConfig;
        public FecCodecConfig FecDecoderConfig;
        public RtpValidatorConfig rtpValidatorConfig;
        public AudioLatencyMonitorConfig audioLatencyMonitorConfig;
        public AudioWatchdogConfig audioWatchdogConfig;
        public AudioResamplerConfig audioResamplerConfig;

        public ReceiverSessionConfig() {
            RocDefaults defaults = new RocDefaults();
            this.targetLatency = defaults.defaultLatency();
            this.channels = defaults.defaultChannelMask();
            this.payloadType = 0;
            this.audioLatencyMonitorConfig.minLatency =
                    this.targetLatency * defaults.defaultMinLatencyFactor();
            this.audioLatencyMonitorConfig.maxLatency =
                    this.targetLatency * defaults.defaultMaxLatencyFactor();
            }
    }

    public class FecReaderConfig {
        public long maxSourceBlockJump;

        public FecReaderConfig(long maxSourceBlockJump) {
            this.maxSourceBlockJump = maxSourceBlockJump;
        }

        public FecReaderConfig() {
            this.maxSourceBlockJump = 100;
        }
    }

    public class FecScheme {
        public static final int NONE = 0;
        public static final int REED_SOLOMON_M8 = 1;
        public static final int LDPC_STAIRCASE = 2;
    }
    public class FecCodecConfig {
        public int fecScheme;
        public int LDPC_prng_Seed;
        public byte LDPC_n1;
        public short rs_m;

        public FecCodecConfig() {
            this.fecScheme = 0;
            this.LDPC_prng_Seed = 1297501556;
            this.LDPC_n1 = 7;
            this.rs_m = 8;
        }
    }

    public class RtpValidatorConfig {
        public long maxSeqNumJump; /* ns */
        public long maxTimeStampJump; /* ns */

        public RtpValidatorConfig() {
            this.maxSeqNumJump = 100;
            this.maxTimeStampJump = 1000 * 1000 * 1000;
        }
    }

    public class AudioLatencyMonitorConfig {
        public long feUpdateInterval; /* ns */
        public long minLatency; /* ns */
        public long maxLatency; /* ns */
        float maxScalingDelta; /* ns */

        public AudioLatencyMonitorConfig() {
            this.feUpdateInterval = 5 * 1000;
            this.minLatency = 0;
            this.maxLatency = 0;
            this.maxScalingDelta = 0.005f;
        }
    }

    public class AudioWatchdogConfig {
        public long noPlaybackTimeout; /* ns */
        public long brokenPlaybackTimeout; /* ns */
        public long breakageDetectionWindow; /* ns */
        public long frameStatusWindow;

        public AudioWatchdogConfig() {
            this.noPlaybackTimeout = 2 * 1000 * 1000;
            this.brokenPlaybackTimeout = 2 * 1000 * 1000;
            this.breakageDetectionWindow = 300 * 1000;
            this.frameStatusWindow = 20;
        }
    }

    public class AudioResamplerConfig {
        public long windowInterp; /* size_t */
        public long windowSize; /* size_t */

        public AudioResamplerConfig() {
            this.windowInterp = 128;
            this.windowSize = 32;
        }
    }

    public class RocReceiverConfig {
        public ReceiverCommonConfig receiverCommonConfig;
        public ReceiverSessionConfig receiverSessionConfig;

        public RocReceiverConfig(ReceiverCommonConfig receiverCommonConfig,
                                 ReceiverSessionConfig receiverSessionConfig) {
            this.receiverCommonConfig = receiverCommonConfig;
            this.receiverSessionConfig = receiverSessionConfig;
        }
    }
}
