package com.github.rocproject.roc;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ContextTest {

    @Test
    public void ContextDefaultConfigTest() {
        assertDoesNotThrow(() -> {
            Context context = new Context();
            context.close();
        });
    }

    @Test
    public void ContextWithInvalidConfigTest() {
        assertThrows(Exception.class, () -> {
            try (Context context = new Context(new ContextConfig.Builder().maxPacketSize(-1).maxFrameSize(-1).build())) {}
        });
    }

    @Test
    public void ContextCloseWithAttachedSender() {
        assertThrows(IOException.class, () -> {
            try (Context context = new Context()) {
                SenderConfig config = new SenderConfig.Builder(44100,
                                                            ChannelSet.STEREO,
                                                            FrameEncoding.PCM_FLOAT)
                                                            .automaticTiming(1)
                                                        .build();
                Sender sender = new Sender(context, config);
            }
        });
    }

    @Test
    public void ContextCloseWithAttachedReceiver() {
        assertThrows(IOException.class, () -> {
            try (Context context = new Context()) {
                ReceiverConfig config = new ReceiverConfig.Builder(44100,
                        ChannelSet.STEREO,
                        FrameEncoding.PCM_FLOAT)
                        .automaticTiming(1)
                        .build();
                Receiver receiver = new Receiver(context, config);
            }
        });
    }
}
