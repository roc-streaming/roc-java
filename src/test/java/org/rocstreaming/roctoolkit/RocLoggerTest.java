package org.rocstreaming.roctoolkit;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.*;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class RocLoggerTest {

    @BeforeEach
    public void beforeEach() {
        RocLogger.setLevel(RocLogLevel.INFO);
    }

    private static Stream<Arguments> testSetLevelProvider() {
        return Stream.of(
                Arguments.of(RocLogLevel.NONE, false, false),
                Arguments.of(RocLogLevel.ERROR, true, false),
                Arguments.of(RocLogLevel.INFO, true, true),
                Arguments.of(RocLogLevel.DEBUG, true, true),
                Arguments.of(RocLogLevel.TRACE, true, true)
        );
    }

    @ParameterizedTest
    @MethodSource("testSetLevelProvider")
    public void testSetLevel(RocLogLevel level, boolean expectError, boolean expectInfo) {
        Map<Level, Integer> msgCount = new ConcurrentHashMap<>();
        Handler handler = wrapHandler(record -> msgCount.compute(record.getLevel(), (k, v) -> v == null ? 1 : v + 1));
        RocLogger.LOGGER.addHandler(handler);

        assertDoesNotThrow(() -> {
            RocLogger.setLevel(level);
            try {
                // trigger error logs
                new Endpoint("invalid");
            } catch (Exception ignored) {
            }
            // trigger info logs
            //noinspection EmptyTryBlock
            try (RocContext ignored = new RocContext()) {
            }
        });
        await().atMost(Duration.FIVE_MINUTES)
                .untilAsserted(() -> {
                    assertEquals(expectError, msgCount.containsKey(Level.SEVERE));
                    assertEquals(expectInfo, msgCount.containsKey(Level.INFO));
                });
        RocLogger.LOGGER.removeHandler(handler);
    }

    @Test
    public void testSetNullLevel() {
        assertThrows(IllegalArgumentException.class, () -> RocLogger.setLevel(null));
    }

    @Test
    public void testSetHandler() throws Exception {
        AtomicBoolean hasLogOpen = new AtomicBoolean();
        AtomicBoolean hasLogClose = new AtomicBoolean();
        Handler handler = wrapHandler(record -> {
            if (!record.getSourceClassName().equals("libroc")) {
                return;
            }
            if (record.getMessage().startsWith("roc_context_open")) {
                hasLogOpen.set(true);
            }
            if (record.getMessage().startsWith("roc_context_close")) {
                hasLogClose.set(true);
            }
        });
        RocLogger.LOGGER.addHandler(handler);

        //noinspection EmptyTryBlock
        try (RocContext ignored = new RocContext()) {
        }

        await().atMost(Duration.FIVE_MINUTES)
                .until(() -> hasLogOpen.get() && hasLogClose.get());

        RocLogger.LOGGER.removeHandler(handler);
    }

    private Handler wrapHandler(Consumer<LogRecord> consumer) {
        return new MemoryHandler(new ConsoleHandler(), 1000, Level.ALL) {
            @Override
            public synchronized void publish(LogRecord record) {
                consumer.accept(record);
            }
        };
    }
}
