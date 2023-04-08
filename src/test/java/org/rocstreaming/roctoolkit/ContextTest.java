package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ContextTest {

    @BeforeAll
    public static void beforeAll() {
        Logger.setLevel(LogLevel.INFO);
    }

    @Test
    public void ContextDefaultConfigTest() {
        assertDoesNotThrow(() -> {
            Context context = new Context();
            context.close();
        });
    }

    @Test
    public void ContextWithInvalidConfigTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            try (Context context = new Context(new ContextConfig.Builder().maxPacketSize(-1).maxFrameSize(-1).build())) {}
        });
    }

    @Test
    public void ContextCloseWithAttachedSender() {
        assertThrows(Exception.class, () -> {
            Sender sender = null;
            try (Context context = new Context()) {
                SenderConfig config = new SenderConfig.Builder(44100,
                                                            ChannelSet.STEREO,
                                                            FrameEncoding.PCM_FLOAT)
                                                        .build();
                sender = new Sender(context, config);
            } finally {
                if (sender != null) sender.close();
            }
        });
    }

    @Test
    public void ContextCloseWithAttachedReceiver() {
        assertThrows(Exception.class, () -> {
            Receiver receiver = null;
            try (Context context = new Context()) {
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
