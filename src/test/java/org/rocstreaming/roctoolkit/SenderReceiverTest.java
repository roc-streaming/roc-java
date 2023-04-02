package org.rocstreaming.roctoolkit;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class SenderReceiverTest {

    private static final int SAMPLE_RATE = 44100;

    private final SenderConfig senderConfig;
    private final ReceiverConfig receiverConfig;

    public SenderReceiverTest() {
        this.senderConfig = new SenderConfig.Builder(SAMPLE_RATE,
                ChannelSet.STEREO,
                FrameEncoding.PCM_FLOAT)
                .build();

        this.receiverConfig = new ReceiverConfig.Builder(SAMPLE_RATE,
                ChannelSet.STEREO,
                FrameEncoding.PCM_FLOAT)
                .build();
    }

    @BeforeAll
    public static void beforeAll() {
        Logger.setLevel(LogLevel.INFO);
    }

    @Test
    void WriteReadTest() throws Exception {
        try (
                Context context = new Context();
                Sender sender = new Sender(context, senderConfig);
                Receiver receiver = new Receiver(context, receiverConfig)
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
