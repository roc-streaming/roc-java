package org.rocstreaming.roctoolkit;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class NativeObjectCleanerTest {

    @BeforeAll
    public static void beforeAll() {
        Logger.setLevel(LogLevel.DEBUG);
    }

    @Test
    void senderAutoClosingTest() throws Exception {
        RocContext context = new RocContext();

        RocSenderConfig config = new RocSenderConfig.Builder(44100, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT).build();
        @SuppressWarnings("unused")
        RocSender sender = new RocSender(context, config);

        Exception exception = assertThrows(Exception.class, context::close);
        assertEquals("Error closing context", exception.getMessage()); // sender still using context

        //noinspection UnusedAssignment
        sender = null;
        System.gc();
        long timeout = TimeUnit.MINUTES.toMillis(3);
        while (timeout > 0) {
            Thread.sleep(50);
            timeout -= 50;
            try {
                assertDoesNotThrow(context::close);
                return;
            } catch (AssertionFailedError ignore) {
            }
            System.gc();
        }
        fail("failed to close context because sender wasn't auto closed");
    }

    @Test
    void receiverAutoClosingTest() throws Exception {
        RocContext context = new RocContext();

        RocReceiverConfig config = new RocReceiverConfig.Builder(44100, ChannelSet.STEREO, FrameEncoding.PCM_FLOAT).build();
        @SuppressWarnings("unused")
        RocReceiver receiver = new RocReceiver(context, config);

        Exception exception = assertThrows(Exception.class, context::close);
        assertEquals("Error closing context", exception.getMessage()); // receiver still using context

        //noinspection UnusedAssignment
        receiver = null;
        System.gc();
        await().atMost(Duration.FIVE_MINUTES)
                .untilAsserted(() -> {
                    System.gc();
                    assertDoesNotThrow(context::close);
                });
    }

}