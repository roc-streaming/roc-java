package org.rocstreaming.roctoolkit.integration;

import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.rocstreaming.roctoolkit.*;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RocSenderReceiverTest extends BaseTest {

    private static final int SAMPLE_RATE = 44100;

    private static final RocContextConfig CONTEXT_CONFIG = RocContextConfig.builder()
            .maxPacketSize(0)
            .maxFrameSize(0)
            .build();

    private static final RocSenderConfig SENDER_CONFIG = RocSenderConfig.builder()
            .frameSampleRate(SAMPLE_RATE)
            .frameChannels(ChannelSet.STEREO)
            .frameEncoding(FrameEncoding.PCM_FLOAT)
            .clockSource(ClockSource.INTERNAL)
            .build();

    private static final RocReceiverConfig RECEIVER_CONFIG = RocReceiverConfig.builder()
            .frameSampleRate(SAMPLE_RATE)
            .frameChannels(ChannelSet.STEREO)
            .frameEncoding(FrameEncoding.PCM_FLOAT)
            .clockSource(ClockSource.INTERNAL)
            .build();

    @Test
    void testWriteRead() throws Exception {
        try (
                RocContext context = new RocContext(CONTEXT_CONFIG);
                RocSender sender = new RocSender(context, SENDER_CONFIG);
                RocReceiver receiver = new RocReceiver(context, RECEIVER_CONFIG)
        ) {

            Endpoint sourceEndpoint = new Endpoint("rtp+rs8m://127.0.0.1:10001");
            Endpoint repairEndpoint = new Endpoint("rs8m://127.0.0.1:10002");

            receiver.bind(Slot.DEFAULT, Interface.AUDIO_SOURCE, sourceEndpoint);
            receiver.bind(Slot.DEFAULT, Interface.AUDIO_REPAIR, repairEndpoint);

            sender.connect(Slot.DEFAULT, Interface.AUDIO_SOURCE, sourceEndpoint);
            sender.connect(Slot.DEFAULT, Interface.AUDIO_REPAIR, repairEndpoint);

            float[] samples = new float[]{0, 1};
            AtomicBoolean running = new AtomicBoolean(true);
            Future<?> submit = Executors.newSingleThreadExecutor().submit(() -> {
                while (running.get()) {
                    try {
                        sender.write(samples);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            float[] readBuffer = new float[2];
            await().atMost(Duration.ONE_MINUTE)
                    .untilAsserted(() -> {
                        receiver.read(readBuffer);
                        assertNotEquals(new float[]{0, 0}, readBuffer);
                    });
            running.set(false);
            submit.get();
        }

    }
}
