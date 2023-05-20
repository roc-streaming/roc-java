package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.*;

public class RocSenderTest {

    private static final int SAMPLE_RATE = 44100;
    private static final RocSenderConfig config = RocSenderConfig.builder()
            .frameSampleRate(SAMPLE_RATE)
            .frameChannels(ChannelSet.STEREO)
            .frameEncoding(FrameEncoding.PCM_FLOAT)
            .build();
    private final int SINE_RATE = 440;
    private final int SINE_SAMPLES = (SAMPLE_RATE * 5);
    private final int BUFFER_SIZE = 100;
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
        this.samples = new float[BUFFER_SIZE];
        gensine(this.samples);
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
    public void testCreationAndDeinitialization() {
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocSender ignored = new RocSender(context, config)) {}
        });
    }

    @Test
    public void testCreationAndDeinitializationWithFullConfig() {
        RocSenderConfig config = RocSenderConfig.builder()
                .frameSampleRate(SAMPLE_RATE)
                .frameChannels(ChannelSet.STEREO)
                .frameEncoding(FrameEncoding.PCM_FLOAT)
                .packetSampleRate(44100)
                .packetChannels(ChannelSet.STEREO)
                .packetEncoding(PacketEncoding.AVP_L16)
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

    private static Stream<Arguments> testInvalidCreationArguments() throws Exception {
        return Stream.of(
                Arguments.of(
                        "context must not be null",
                        IllegalArgumentException.class,
                        null,
                        config),
                Arguments.of(
                        "config must not be null",
                        IllegalArgumentException.class,
                        new RocContext(),
                        null)
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidCreationArguments")
    public void testInvalidCreation(String errorMessage, Class<Exception> exceptionClass, RocContext context, RocSenderConfig config) {
        Exception exception = assertThrows(exceptionClass, () -> new RocSender(context, config));
        assertEquals(errorMessage, exception.getMessage());
    }

    private static Stream<Arguments> testInvalidSetOutgoingAddressArguments() {
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
    @MethodSource("testInvalidSetOutgoingAddressArguments")
    public void testInvalidSetOutgoingAddress(String errorMessage, Slot slot, Interface iface, String ip) throws Exception {
        try (RocSender receiver = new RocSender(context, config)) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> receiver.setOutgoingAddress(slot, iface, ip)
            );
            assertEquals(errorMessage, exception.getMessage());
        }
    }

    @Test
    void testSetOutgoingAddressAfterConnect() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            Exception exception = assertThrows(Exception.class, () -> sender.setOutgoingAddress(Slot.DEFAULT, Interface.AUDIO_SOURCE, "127.0.0.1"));
            assertEquals("Can't set outgoing address", exception.getMessage());
        }
    }

    @Disabled("bind not implemented in roc 0.2.x yet")
    @Test
    public void testBind() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            assertDoesNotThrow(() -> sender.bind(new Endpoint("rtp+rs8m://127.0.0.1:0")));
        }
    }

    @Disabled("bind not implemented in roc 0.2.x yet")
    @Test
    public void testBindEphemeralPort() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            Endpoint senderEndpoint = new Endpoint("rtp+rs8m://127.0.0.1:0");
            sender.bind(senderEndpoint);
            assertNotEquals(0, senderEndpoint.getPort());
        }
    }

    @Disabled("bind not implemented in roc 0.2.x yet")
    @Test
    public void testInvalidBind() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            assertThrows(IllegalArgumentException.class, () -> sender.bind(null));
            sender.bind(new Endpoint("rtp+rs8m://127.0.0.1:0"));
            assertThrows(IOException.class, () -> {
                sender.bind(new Endpoint("rtp+rs8m://127.0.0.1:0"));
            });
        }
    }

    @Test
    public void testConnect() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            assertDoesNotThrow(() -> {
                sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:10001"));
                sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://127.0.0.1:10002"));
            });
        }
    }

    private static Stream<Arguments> testInvalidConnectArguments() {
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
    @MethodSource("testInvalidConnectArguments")
    public void testInvalidConnect(String errorMessage, Slot slot, Interface iface, Endpoint endpoint) throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sender.connect(slot, iface, endpoint)
            );
            assertEquals(errorMessage, exception.getMessage());
        }
    }

    @Test
    public void testWrite() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            for (int i = 0; i < SINE_SAMPLES / BUFFER_SIZE; i++) {
                assertDoesNotThrow(() -> sender.write(samples));
            }
        }
    }

    @Test
    public void testInvalidWrite() throws Exception {
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
    public void testInvalidConnectAfterWrite() throws Exception {
        try (RocSender sender = new RocSender(context, config)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            sender.write(samples);
            assertThrows(IOException.class, () -> {
                sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            });
        }
    }

}
