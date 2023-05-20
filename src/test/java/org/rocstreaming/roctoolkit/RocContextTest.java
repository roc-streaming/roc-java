package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RocContextTest {

    @BeforeAll
    public static void beforeAll() {
        RocLogger.setLevel(RocLogLevel.INFO);
    }

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
                RocSenderConfig config = RocSenderConfig.builder()
                        .frameSampleRate(44100)
                        .frameChannels(ChannelSet.STEREO)
                        .frameEncoding(FrameEncoding.PCM_FLOAT)
                        .build();
                sender = new RocSender(context, config);
            } finally {
                if (sender != null) sender.close();
            }
        });
    }

    @Test
    public void testCloseWithAttachedReceiver() {
        assertThrows(Exception.class, () -> {
            RocReceiver receiver = null;
            try (RocContext context = new RocContext()) {
                RocReceiverConfig config = RocReceiverConfig.builder()
                        .frameSampleRate(44100)
                        .frameChannels(ChannelSet.STEREO)
                        .frameEncoding(FrameEncoding.PCM_FLOAT)
                        .build();
                receiver = new RocReceiver(context, config);
            } finally {
                if (receiver != null) receiver.close();
            }
        });
    }
}
