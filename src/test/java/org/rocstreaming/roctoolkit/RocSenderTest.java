package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.time.Duration;
import java.util.stream.Stream;

import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.*;

public class RocSenderTest extends BaseTest {

    private static final int SAMPLE_RATE = 44100;
    public static final RocSenderConfig CONFIG = RocSenderConfig.builder()
            .frameEncoding(
                    MediaEncoding.builder()
                            .rate(SAMPLE_RATE)
                            .format(Format.PCM_FLOAT32)
                            .channels(ChannelLayout.STEREO)
                            .build()
            )
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
            try (RocSender ignored = new RocSender(context, CONFIG)) {
            }
        });
    }

    @Test
    public void testCreationAndDeinitializationWithFullConfig() {
        RocSenderConfig config = RocSenderConfig.builder()
                .frameEncoding(
                        MediaEncoding.builder()
                                .rate(SAMPLE_RATE)
                                .format(Format.PCM_FLOAT32)
                                .channels(ChannelLayout.STEREO)
                                .build()
                )
                .packetEncoding(PacketEncoding.AVP_L16_STEREO)
                .packetLength(Duration.ofNanos(2000))
                .packetInterleaving(1)
                .fecEncoding(FecEncoding.RS8M)
                .fecBlockSourcePackets(10)
                .fecBlockRepairPackets(10)
                .clockSource(ClockSource.INTERNAL)
                .resamplerBackend(ResamplerBackend.BUILTIN)
                .resamplerProfile(ResamplerProfile.HIGH)
                .build();
        assertDoesNotThrow(() -> {
            //noinspection EmptyTryBlock
            try (RocSender ignored = new RocSender(context, config)) {
            }
        });
    }

    private static Stream<Arguments> invalidCreationArguments() throws Exception {
        return Stream.of(
                Arguments.of(
                        "context must not be null",
                        IllegalArgumentException.class,
                        null,
                        CONFIG),
                Arguments.of(
                        "config must not be null",
                        IllegalArgumentException.class,
                        new RocContext(),
                        null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCreationArguments")
    public void testInvalidCreation(String errorMessage, Class<Exception> exceptionClass, RocContext context, RocSenderConfig config) {
        Exception exception = assertThrows(exceptionClass, () -> new RocSender(context, config));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testConfigureBeforeConnect() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            InterfaceConfig ifaceConfig = InterfaceConfig.builder()
                    .outgoingAddress("127.0.0.1")
                    .build();
            assertDoesNotThrow(() -> sender.configure(Slot.DEFAULT, Interface.AUDIO_SOURCE, ifaceConfig));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
        }
    }

    @Test
    void testConfigureAfterConnect() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            InterfaceConfig ifaceConfig = InterfaceConfig.builder()
                    .outgoingAddress("127.0.0.1")
                    .build();
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            Exception exception = assertThrows(Exception.class, () -> sender.configure(Slot.DEFAULT, Interface.AUDIO_SOURCE, ifaceConfig));
            assertEquals("Error configuring sender", exception.getMessage());
        }
    }

    private static Stream<Arguments> invalidConfigureArguments() {
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
                        "0.0.0.0")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidConfigureArguments")
    public void testInvalidConfigure(String errorMessage, Slot slot, Interface iface, String ip) throws Exception {
        try (RocSender receiver = new RocSender(context, CONFIG)) {
            InterfaceConfig ifaceConfig = InterfaceConfig.builder()
                    .outgoingAddress(ip)
                    .build();
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> receiver.configure(slot, iface, ifaceConfig)
            );
            assertEquals(errorMessage, exception.getMessage());
        }
    }

    @Test
    public void testConnect() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            assertDoesNotThrow(() -> {
                sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:10001"));
                sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://127.0.0.1:10002"));
            });
        }
    }

    private static Stream<Arguments> invalidConnectArguments() {
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
    @MethodSource("invalidConnectArguments")
    public void testInvalidConnect(String errorMessage, Slot slot, Interface iface, Endpoint endpoint) throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sender.connect(slot, iface, endpoint)
            );
            assertEquals(errorMessage, exception.getMessage());
        }
    }

    @Test
    public void testUnlink() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            assertDoesNotThrow(() -> {
                Slot slot1 = new Slot(1);
                Slot slot2 = new Slot(2);
                sender.connect(slot1, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:10001"));
                sender.connect(slot2, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:10002"));
                sender.unlink(slot1);
                sender.unlink(slot2);
            });
        }
    }

    @Test
    public void testInvalidUnlink() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            assertDoesNotThrow(() -> {
                Slot slot1 = new Slot(1);
                Slot slot2 = new Slot(2);
                sender.connect(slot1, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:10001"));
                sender.connect(slot2, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://127.0.0.1:10002"));
                sender.unlink(slot1);
                assertThrows(IllegalArgumentException.class, () -> sender.unlink(slot1));
                sender.unlink(slot2);
                assertThrows(IllegalArgumentException.class, () -> sender.unlink(slot2));
            });
        }
    }

    @Test
    public void testWrite() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            for (int i = 0; i < SINE_SAMPLES / BUFFER_SIZE; i++) {
                assertDoesNotThrow(() -> sender.write(samples));
            }
        }
    }

    @Test
    public void testInvalidWrite() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            assertThrows(IllegalArgumentException.class, () -> sender.write(null));
        }
    }

    @Test
    public void testInvalidConnectAfterWrite() throws Exception {
        try (RocSender sender = new RocSender(context, CONFIG)) {
            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, new Endpoint("rs8m://0.0.0.0:10002"));
            sender.write(samples);
            assertThrows(IOException.class, () -> {
                sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, new Endpoint("rtp+rs8m://0.0.0.0:10001"));
            });
        }
    }

}
