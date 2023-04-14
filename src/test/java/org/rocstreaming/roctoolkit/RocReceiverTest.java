package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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
        RocLogger.setLevel(RocLogLevel.INFO);
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

    private static Stream<Arguments> testInvalidReceiverCreationArguments() throws Exception {
        return Stream.of(
                Arguments.of(
                        "context must not be null",
                        IllegalArgumentException.class,
                        null,
                        new RocReceiverConfig.Builder(SAMPLE_RATE, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT).build()),
                Arguments.of(
                        "config must not be null",
                        IllegalArgumentException.class,
                        new RocContext(),
                        null),
                Arguments.of(
                        "Bad config argument",
                        IllegalArgumentException.class,
                        new RocContext(),
                        new RocReceiverConfig.Builder(-1, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT).build()),
                Arguments.of(
                        "Error opening receiver",
                        Exception.class,
                        new RocContext(),
                        new RocReceiverConfig.Builder(SAMPLE_RATE, null, FrameEncoding.PCM_FLOAT).build()),
                Arguments.of(
                        "Error opening receiver",
                        Exception.class,
                        new RocContext(),
                        new RocReceiverConfig.Builder(SAMPLE_RATE, ChannelSet.STEREO, null).build())
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidReceiverCreationArguments")
    public void TestInvalidReceiverCreation(String errorMessage, Class<Exception> exceptionClass, RocContext context, RocReceiverConfig config) {
        Exception exception = assertThrows(exceptionClass, () -> new RocReceiver(context, config));
        assertEquals(errorMessage, exception.getMessage());
    }

    private static Stream<Arguments> testInvalidReceiverSetMulticastGroupArguments() {
        return Stream.of(
                Arguments.of(
                        "slot must not be null",
                        null,
                        Interface.AUDIO_SOURCE,
                        "0.0.0.0"),
                Arguments.of(
                        "iface must not be null",
                        Slot.DEFAULT,
                        null,
                        "0.0.0.0"),
                Arguments.of(
                        "ip must not be empty",
                        Slot.DEFAULT,
                        Interface.AUDIO_SOURCE,
                        null)
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidReceiverSetMulticastGroupArguments")
    public void TestInvalidReceiverSetMulticastGroup(String errorMessage, Slot slot, Interface iface, String ip) throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> receiver.setMulticastGroup(slot, iface, ip)
            );
            assertEquals(errorMessage, exception.getMessage());
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

    private static Stream<Arguments> testInvalidReceiverBindArguments() {
        return Stream.of(
                Arguments.of(
                        "slot must not be null",
                        null,
                        Interface.AUDIO_SOURCE,
                        new Endpoint("rtsp://0.0.0.0")),
                Arguments.of(
                        "iface must not be null",
                        Slot.DEFAULT,
                        null,
                        new Endpoint("rtsp://0.0.0.0")),
                Arguments.of(
                        "endpoint must not be null",
                        Slot.DEFAULT,
                        Interface.AUDIO_SOURCE,
                        null)
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidReceiverBindArguments")
    public void TestInvalidReceiverBind(String errorMessage, Slot slot, Interface iface, Endpoint endpoint) throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> receiver.bind(slot, iface, endpoint)
            );
            assertEquals(errorMessage, exception.getMessage());
        }
    }

    @Test
    public void TestInvalidReadFloatArray() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, config)) {
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:0"));
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://127.0.0.1:0"));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> receiver.read(null));
            assertEquals("samples must not be null", exception.getMessage());
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
