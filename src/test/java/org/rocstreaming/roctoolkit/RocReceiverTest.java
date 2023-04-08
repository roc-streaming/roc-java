package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RocReceiverTest {

    private static final int SAMPLE_RATE = 44100;
    private final RocReceiverConfig config;
    private RocContext context;

    RocReceiverTest() {
        this.config = new RocReceiverConfig.Builder(SAMPLE_RATE,
                ChannelSet.STEREO,
                FrameEncoding.PCM_FLOAT)
                .build();
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
    public void TestValidReceiverCreationAndDeinitialization() {
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocReceiver ignored = new RocReceiver(context, config)) {
            }
        });
    }

    @Test
    public void TestValidReceiverCreationAndDeinitializationWithFullConfig() {
        RocReceiverConfig config = new RocReceiverConfig.Builder(SAMPLE_RATE, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT)
                .clockSource(ClockSource.INTERNAL)
                .resamplerBackend(ResamplerBackend.BUILTIN)
                .resamplerProfile(ResamplerProfile.HIGH)
                .targetLatency(1000)
                .maxLatencyOverrun(500)
                .maxLatencyUnderrun(500)
                .noPlaybackTimeout(2000)
                .brokenPlaybackTimeout(2000)
                .breakageDetectionWindow(2000)
                .build();
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocReceiver ignored = new RocReceiver(context, config)) {
            }
        });
    }

    @Test
    @SuppressWarnings("resource")
    public void TestInvalidReceiverCreation() {
        assertThrows(IllegalArgumentException.class, () -> new RocReceiver(null, config));
        assertThrows(IllegalArgumentException.class, () -> new RocReceiver(context, null));
        assertThrows(IllegalArgumentException.class, () -> {
            RocReceiverConfig config = new RocReceiverConfig.Builder(-1, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT).build();
            new RocReceiver(context, config);
        });
        assertThrows(Exception.class, () -> {
            RocReceiverConfig config = new RocReceiverConfig.Builder(SAMPLE_RATE, null, FrameEncoding.PCM_FLOAT).build();
            new RocReceiver(context, config);
        });
        assertThrows(Exception.class, () -> {
            RocReceiverConfig config = new RocReceiverConfig.Builder(SAMPLE_RATE, ChannelSet.STEREO, null).build();
            new RocReceiver(context, config);
        });
    }

    @Test
    public void TestValidReceiverBind() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            assertDoesNotThrow(() -> receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:0")));
            assertDoesNotThrow(() -> receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:0")));
        }
    }

    @Test
    public void TestReceiverBindEphemeralPort() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            Endpoint sourceEndpoint = new Endpoint("rtp+rs8m://0.0.0.0:0");
            Endpoint repairEndpoint = new Endpoint("rs8m://0.0.0.0:0");
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, sourceEndpoint);
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, repairEndpoint);
            //
            int sourcePort = sourceEndpoint.getPort();
            int repairPort = repairEndpoint.getPort();
            assertNotEquals(0, sourcePort);
            assertNotEquals(0, repairPort);
            assertEquals("rtp+rs8m://0.0.0.0:" + sourcePort, sourceEndpoint.getUri());
            assertEquals("rs8m://0.0.0.0:" + repairPort, repairEndpoint.getUri());
        }
    }

    @Test
    public void TestInvalidReceiverBind() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(null, Interface.AUDIO_SOURCE, new Endpoint("rtp://0.0.0.0")));
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(Slot.DEFAULT, null, new Endpoint("rtp://0.0.0.0")));
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, null));
        }
    }

    @Test
    public void TestInvalidReadFloatArray() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:0"));
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://127.0.0.1:0"));
            assertThrows(IllegalArgumentException.class, () -> receiver.read(null));
        }
    }

    @Test
    public void TestReceiverSetMulticastGroup() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            assertDoesNotThrow(() -> {
                receiver.setMulticastGroup(Slot.DEFAULT, Interface.AUDIO_SOURCE, "0.0.0.0");
                receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://224.0.0.1:0"));
            });
        }
    }

    @Test
    public void TestReceiverReadZeroizedArray() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:0"));
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:0"));
            float[] samples = {1.0f, 1.0f};
            receiver.read(samples);
            assertArrayEquals(new float[]{0.0f, 0.0f}, samples);
        }
    }
}
