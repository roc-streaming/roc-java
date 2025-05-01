package org.rocstreaming.roctoolkit;

import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class NativeObjectCleanerTest extends BaseTest {

    @Test
    void senderAutoClosingTest() throws Exception {
        RocContext context = new RocContext();

        @SuppressWarnings("unused")
        RocSender sender = new RocSender(context, RocSenderTest.CONFIG);

        Exception exception = assertThrows(IllegalStateException.class, context::close);
        assertEquals("Can't close RocContext before closing associated RocSender/RocReceiver(s)",
                exception.getMessage()); // sender still using context

        //noinspection UnusedAssignment
        sender = null;
        System.gc();
        await().atMost(Duration.FIVE_MINUTES)
                .untilAsserted(() -> {
                    System.gc();
                    assertDoesNotThrow(context::close);
                    Thread.sleep(50);
                });
    }

    @Test
    void receiverAutoClosingTest() throws Exception {
        RocContext context = new RocContext();

        @SuppressWarnings("unused")
        RocReceiver receiver = new RocReceiver(context, RocReceiverTest.CONFIG);

        Exception exception = assertThrows(IllegalStateException.class, context::close);
        assertEquals("Can't close RocContext before closing associated RocSender/RocReceiver(s)",
                exception.getMessage()); // receiver still using context

        //noinspection UnusedAssignment
        receiver = null;
        System.gc();
        await().atMost(Duration.FIVE_MINUTES)
                .untilAsserted(() -> {
                    System.gc();
                    assertDoesNotThrow(context::close);
                    Thread.sleep(50);
                });
    }

}
