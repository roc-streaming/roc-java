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


public class RocSenderReceiverTest {

    @BeforeAll
    public static void beforeAll() {
        RocLogger.setLevel(RocLogLevel.INFO);
    }

    @Test
    void testWriteRead() throws Exception {
        try (
                RocContext context = new RocContext();
                RocSender sender = new RocSender(context, RocSenderTest.CONFIG);
                RocReceiver receiver = new RocReceiver(context, RocReceiverTest.CONFIG)
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
