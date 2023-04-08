package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.*;

import java.io.IOException;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.*;

public class RocSenderTest {

    private final int SAMPLE_RATE = 44100;
    private final int SINE_RATE = 440;
    private final int SINE_SAMPLES = (SAMPLE_RATE * 5);
    private final int BUFFER_SIZE = 100;
    private RocSenderConfig config;
    private float[] samples;
    private RocContext context;

    private void gensine(float[] samples) {
        double t = 0d;
        for (int i = 0; i < samples.length / 2; i++) {
            float s = (float) sin(2 * 3.14159265359 * SINE_RATE / SAMPLE_RATE * t);
            /* Fill samples for left and right channels. */
            samples[i * 2] = s;
            samples[i * 2 + 1] = -s;
            t += 1;
        }
    }

    RocSenderTest() {
        this.config = new RocSenderConfig.Builder(SAMPLE_RATE,
                                                ChannelSet.STEREO,
                                                FrameEncoding.PCM_FLOAT)
                                        .build();
        this.samples = new float[BUFFER_SIZE];
        gensine(this.samples);
    }

    @BeforeAll
    public static void beforeAll() {
        Logger.setLevel(LogLevel.INFO);
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        this.context = new RocContext();
    }

    @AfterEach
    public void afterEach() throws Exception {
        this.context.close();
    }

    @Test
    public void TestValidSenderCreationAndDeinitialization() {
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocSender ignored = new RocSender(context, config)) {}
        });
    }

    @Test
    public void TestValidSenderCreationAndDeinitializationWithFullConfig() {
        RocSenderConfig config = new RocSenderConfig.Builder(SAMPLE_RATE, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT)
                .packetSampleRate(44100)
                .packetChannels(ChannelSet.STEREO)
                .packetLength(2000)
                .packetInterleaving(1)
                .clockSource(ClockSource.INTERNAL)
                .resamplerBackend(ResamplerBackend.BUILTIN)
                .resamplerProfile(ResamplerProfile.HIGH)
                .fecEncoding(FecEncoding.RS8M)
                .fecBlockSourcePackets(10)
                .fecBlockRepairPackets(10)
                .build();
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocSender ignored = new RocSender(context, config)) {
            }
        });
    }

    @Test
    @SuppressWarnings("resource")
    public void TestInvalidSenderCreation() {
        assertThrows(IllegalArgumentException.class, () -> new RocSender(null, config));
        assertThrows(IllegalArgumentException.class, () -> new RocSender(context, null));
        assertThrows(IllegalArgumentException.class, () -> {
            RocSenderConfig config = new RocSenderConfig.Builder(-1, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT).build();
            new RocSender(context, config);
        });

        assertThrows(Exception.class, () -> {
            RocSenderConfig config = new RocSenderConfig.Builder(SAMPLE_RATE, null, FrameEncoding.PCM_FLOAT).build();
            new RocSender(context, config);
        });
        assertThrows(Exception.class, () -> {
            RocSenderConfig config = new RocSenderConfig.Builder(SAMPLE_RATE, ChannelSet.STEREO, null).build();
            new RocSender(context, config);
        });
    }

    @Disabled("bind not implemented in roc 0.2.x yet")
    @Test
    public void TestValidSenderBind() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            assertDoesNotThrow(() -> sender.bind(new Endpoint("rtp+rs8m://127.0.0.1:0")));
        }
    }

    @Disabled("bind not implemented in roc 0.2.x yet")
    @Test
    public void TestSenderBindEphemeralPort() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            Endpoint senderEndpoint = new Endpoint("rtp+rs8m://127.0.0.1:0");
            sender.bind(senderEndpoint);
            assertNotEquals(0, senderEndpoint.getPort());
        }
    }

    @Disabled("bind not implemented in roc 0.2.x yet")
    @Test
    public void TestInvalidSenderBind() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            assertThrows(IllegalArgumentException.class, () -> sender.bind(null));
            sender.bind(new Endpoint("rtp+rs8m://127.0.0.1:0"));
            assertThrows(IOException.class, () -> {
                sender.bind(new Endpoint("rtp+rs8m://127.0.0.1:0"));
            });
        }
    }

    @Test
    public void TestValidSenderConnect() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            assertDoesNotThrow(() -> {
                sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:10001"));
                sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://127.0.0.1:10002"));
            });
        }
    }

    @Test
    public void TestInvalidSenderConnect() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            assertThrows(IllegalArgumentException.class, () -> {
                sender.connect(null, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            });
            assertThrows(IllegalArgumentException.class, () -> {
                sender.connect(Slot.DEFAULT, null, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            });
            assertThrows(IllegalArgumentException.class, () -> {
                sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, null);
            });
        }
    }

    @Test
    public void TestValidSenderWriteFloatArray() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            for (int i = 0; i < SINE_SAMPLES / BUFFER_SIZE; i++) {
                assertDoesNotThrow(() -> sender.write(samples));
            }
        }
    }

    @Test
    public void TestInvalidSenderWriteFloatArray() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            // bind not implemented in roc 0.2.x yet
            // assertThrows(IOException.class, () -> sender.write(samples)); // write before bind
            // sender.bind();
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            assertThrows(IllegalArgumentException.class, () -> sender.write(null));
        }
    }

    @Test
    public void TestInvalidConnectAfterWrite() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            sender.write(samples);
            assertThrows(IOException.class, () -> {
                sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            });
        }
    }

    @Test
    void TestSetOutgoingAddressAfterConnect() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            Exception exception = assertThrows(Exception.class, () -> sender.setOutgoingAddress(Slot.DEFAULT, Interface.AUDIO_SOURCE, "127.0.0.1"));
            assertEquals("Can't set outgoing address", exception.getMessage());
        }
    }
}
