package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RocContextTest extends BaseTest {

    @Test
    public void testWithDefaultConfig() {
        assertDoesNotThrow(() -> {
            RocContext context = new RocContext();
            context.close();
        });
    }

    @Test
    public void testWithNullConfig() {
        //noinspection resource
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new RocContext(null));
        assertEquals("config must not be null", e.getMessage());
    }

    @Test
    public void testCloseWithAttachedSender() {
        assertThrows(Exception.class, () -> {
            RocSender sender = null;
            try (RocContext context = new RocContext()) {
                sender = new RocSender(context, RocSenderTest.CONFIG);
            } finally {
                if (sender != null)
                    sender.close();
            }
        });
    }

    @Test
    public void testCloseWithAttachedReceiver() {
        assertThrows(Exception.class, () -> {
            RocReceiver receiver = null;
            try (RocContext context = new RocContext()) {
                receiver = new RocReceiver(context, RocReceiverTest.CONFIG);
            } finally {
                if (receiver != null)
                    receiver.close();
            }
        });
    }

    private static MediaEncoding validEncoding() {
        return MediaEncoding.builder()
                .rate(44100)
                .format(Format.PCM_FLOAT32)
                .channels(ChannelLayout.STEREO)
                .build();
    }

    @Test
    public void testRegisterEncoding() {
        assertDoesNotThrow(() -> {
            try (RocContext context = new RocContext()) {
                context.registerEncoding(100, validEncoding());
            }
        });
    }

    private static Stream<Arguments> invalidRegisterEncodingArguments() {
        return Stream.of(
                Arguments.of("encodingId must be in range [1; 127]", -1, validEncoding()),
                Arguments.of("encodingId must be in range [1; 127]", 0, validEncoding()),
                Arguments.of("encodingId must be in range [1; 127]", 128, validEncoding()),
                Arguments.of("encoding must not be null", 100, null),
                // encoding id already registered
                Arguments.of("Error registering encoding", PacketEncoding.AVP_L16_MONO.getValue(), validEncoding())
        );
    }

    @ParameterizedTest()
    @MethodSource("invalidRegisterEncodingArguments")
    public void testInvalidEncoding(String error, int encodingId, MediaEncoding encoding) {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            try (RocContext context = new RocContext()) {
                context.registerEncoding(encodingId, encoding);
            }
        });
        assertEquals(error, e.getMessage());
    }
}
