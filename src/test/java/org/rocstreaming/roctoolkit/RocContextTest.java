package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RocContextTest {

    @BeforeAll
    public static void beforeAll() {
        Logger.setLevel(LogLevel.INFO);
    }

    @Test
    public void ContextDefaultConfigTest() {
        assertDoesNotThrow(() -> {
            RocContext context = new RocContext();
            context.close();
        });
    }

    @Test
    public void ContextWithInvalidConfigTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            try (RocContext context = new RocContext(new RocContextConfig.Builder().maxPacketSize(-1).maxFrameSize(-1).build())) {}
        });
    }

    @Test
    public void ContextCloseWithAttachedSender() {
        assertThrows(Exception.class, () -> {
            RocSender sender = null;
            try (RocContext context = new RocContext()) {
                RocSenderConfig config = new RocSenderConfig.Builder(44100,
                                                            ChannelSet.STEREO,
                                                            FrameEncoding.PCM_FLOAT)
                                                        .build();
                sender = new RocSender(context, config);
            } finally {
                if (sender != null) sender.close();
            }
        });
    }

    @Test
    public void ContextCloseWithAttachedReceiver() {
        assertThrows(Exception.class, () -> {
            Receiver receiver = null;
            try (RocContext context = new RocContext()) {
                ReceiverConfig config = new ReceiverConfig.Builder(44100,
                                                                    ChannelSet.STEREO,
                                                                    FrameEncoding.PCM_FLOAT)
                                                            .build();
                receiver = new Receiver(context, config);
            } finally {
                if (receiver != null) receiver.close();
            }
        });
    }
}
