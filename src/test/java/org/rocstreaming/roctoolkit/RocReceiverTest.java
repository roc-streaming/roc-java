package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RocReceiverTest extends BaseTest {

    private static final int SAMPLE_RATE = 44100;
    public static final RocReceiverConfig CONFIG = RocReceiverConfig.builder()
            .frameEncoding(
                    MediaEncoding.builder()
                            .rate(SAMPLE_RATE)
                            .format(Format.PCM_FLOAT32)
                            .channels(ChannelLayout.STEREO)
                            .build()
            )
            .build();
    private RocContext context;

    @BeforeEach
    public void beforeEach() throws Exception {
        this.context = new RocContext();
    }

    @AfterEach
    public void afterEach() {
        this.context.close();
    }

    @Test
    public void testCreationAndDeinitialization() {
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocReceiver ignored = new RocReceiver(context, CONFIG)) {
            }
        });
    }

    @Test
    public void testCreationAndDeinitializationWithFullConfig() {
        RocReceiverConfig config = RocReceiverConfig.builder()
                .frameEncoding(
                        MediaEncoding.builder()
                                .rate(SAMPLE_RATE)
                                .format(Format.PCM_FLOAT32)
                                .channels(ChannelLayout.STEREO)
                                .build()
                )
                .clockSource(ClockSource.INTERNAL)
                .resamplerBackend(ResamplerBackend.BUILTIN)
                .resamplerProfile(ResamplerProfile.HIGH)
                .targetLatency(Duration.ofNanos(1000))
                .latencyTolerance(Duration.ofNanos(500))
                .noPlaybackTimeout(Duration.ofNanos(2000))
                .choppyPlaybackTimeout(Duration.ofNanos(2000))
                .build();
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocReceiver ignored = new RocReceiver(context, config)) {
            }
        });
    }

    private static Stream<Arguments> invalidCreationArguments() throws Exception {
        return Stream.of(
                Arguments.of(
                        "Invalid RocContext: must not be null",
                        IllegalArgumentException.class,
                        null,
                        CONFIG),
                Arguments.of(
                        "Invalid RocReceiverConfig: must not be null",
                        IllegalArgumentException.class,
                        new RocContext(),
                        null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCreationArguments")
    public void testInvalidCreation(String errorMessage, Class<Exception> exceptionClass, RocContext context, RocReceiverConfig config) {
        Exception exception = assertThrows(exceptionClass, () -> new RocReceiver(context, config));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void testConfigureBeforeBind() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            InterfaceConfig ifaceConfig = InterfaceConfig.builder()
                    .outgoingAddress("0.0.0.0")
                    .build();
            assertDoesNotThrow(() -> {
                receiver.configure(Slot.DEFAULT, Interface.AUDIO_SOURCE, ifaceConfig);
                receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://224.0.0.1:0"));
            });
        }
    }

    @Test
    public void testConfigureAfterBind() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            InterfaceConfig ifaceConfig = InterfaceConfig.builder()
                    .outgoingAddress("0.0.0.0")
                    .build();
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://224.0.0.1:0"));
            Exception exception = assertThrows(RocException.class, () -> receiver.configure(Slot.DEFAULT, Interface.AUDIO_SOURCE, ifaceConfig));
            assertEquals("Failed to configure RocReceiver interface", exception.getMessage());
        }
    }

    private static Stream<Arguments> invalidConfigureArguments() {
        return Stream.of(
                Arguments.of(
                        "Invalid Slot: must not be null",
                        null,
                        Interface.AUDIO_SOURCE,
                        InterfaceConfig.builder()
                                .outgoingAddress("0.0.0.0")
                                .build()),
                Arguments.of(
                        "Invalid Interface: must not be null",
                        Slot.DEFAULT,
                        null,
                        InterfaceConfig.builder()
                                .outgoingAddress("0.0.0.0")
                                .build()),
                Arguments.of(
                        "Invalid InterfaceConfig: must not be null",
                        Slot.DEFAULT,
                        Interface.AUDIO_SOURCE,
                        null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidConfigureArguments")
    public void testInvalidConfigure(String errorMessage, Slot slot, Interface iface, InterfaceConfig config) throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> receiver.configure(slot, iface, config)
            );
            assertEquals(errorMessage, exception.getMessage());
        }
    }

    @Test
    public void testBind() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            assertDoesNotThrow(() -> receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:0")));
            assertDoesNotThrow(() -> receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:0")));
        }
    }

    @Test
    public void testBindEphemeralPort() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            Endpoint sourceEndpoint = new Endpoint("rtp+rs8m://0.0.0.0:0");
            Endpoint repairEndpoint = new Endpoint("rs8m://0.0.0.0:0");
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, sourceEndpoint);
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, repairEndpoint);

            int sourcePort = sourceEndpoint.getPort();
            int repairPort = repairEndpoint.getPort();
            assertNotEquals(0, sourcePort);
            assertNotEquals(0, repairPort);
            assertEquals("rtp+rs8m://0.0.0.0:" + sourcePort, sourceEndpoint.getUri());
            assertEquals("rs8m://0.0.0.0:" + repairPort, repairEndpoint.getUri());
        }
    }

    private static Stream<Arguments> invalidBindArguments() {
        return Stream.of(
                Arguments.of(
                        "Invalid Slot: must not be null",
                        null,
                        Interface.AUDIO_SOURCE,
                        new Endpoint("rtsp://0.0.0.0")),
                Arguments.of(
                        "Invalid Interface: must not be null",
                        Slot.DEFAULT,
                        null,
                        new Endpoint("rtsp://0.0.0.0")),
                Arguments.of(
                        "Invalid Endpoint: must not be null",
                        Slot.DEFAULT,
                        Interface.AUDIO_SOURCE,
                        null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBindArguments")
    public void testInvalidBind(String errorMessage, Slot slot, Interface iface, Endpoint endpoint) throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> receiver.bind(slot, iface, endpoint)
            );
            assertEquals(errorMessage, exception.getMessage());
        }
    }

    @Test
    public void testUnlink() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            assertDoesNotThrow(() -> {
                Slot slot1 = new Slot(1);
                Slot slot2 = new Slot(2);
                receiver.bind(slot1, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:0"));
                receiver.bind(slot2, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:0"));
                receiver.unlink(slot1);
                receiver.unlink(slot2);
            });
        }
    }

    @Test
    public void testInvalidUnlink() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            assertDoesNotThrow(() -> {
                Slot slot1 = new Slot(1);
                Slot slot2 = new Slot(2);
                receiver.bind(slot1, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:0"));
                receiver.bind(slot2, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:0"));
                receiver.unlink(slot1);
                assertThrows(RocException.class, () -> receiver.unlink(slot1));
                receiver.unlink(slot2);
                assertThrows(RocException.class, () -> receiver.unlink(slot2));
            });
        }
    }

    @Test
    public void testRead() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:0"));
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:0"));
            float[] samples = {1.0f, 1.0f};
            receiver.read(samples);
            assertArrayEquals(new float[]{0.0f, 0.0f}, samples);
        }
    }

    @Test
    public void testInvalidRead() throws Exception {
        try (RocReceiver receiver = new RocReceiver(context, CONFIG)) {
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:0"));
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://127.0.0.1:0"));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> receiver.read(null));
            assertEquals("Invalid samples: must not be null", exception.getMessage());
        }
    }
}
