package org.rocstreaming.roctoolkit;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class RocLoggerTest {

    @BeforeEach
    public void beforeEach() {
        RocLogger.setLevel(RocLogLevel.INFO);
    }

    private static Stream<Arguments> TestValidLoggerSetLevelProvider() {
        return Stream.of(
                Arguments.of(RocLogLevel.NONE, false, false),
                Arguments.of(RocLogLevel.ERROR, true, false),
                Arguments.of(RocLogLevel.INFO, true, true),
                Arguments.of(RocLogLevel.DEBUG, true, true),
                Arguments.of(RocLogLevel.TRACE, true, true)
        );
    }

    @ParameterizedTest
    @MethodSource("TestValidLoggerSetLevelProvider")
    public void TestValidLoggerSetLevel(RocLogLevel level, boolean expectError, boolean expectInfo) {
        Map<RocLogLevel, Integer> msgCount = new ConcurrentHashMap<>();
        RocLogHandler handler = (lvl, component, message) -> msgCount.compute(lvl, (k, v) -> v == null ? 1 : v + 1);
        RocLogger.setHandler(handler);

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
                    assertEquals(expectError, msgCount.containsKey(RocLogLevel.ERROR));
                    assertEquals(expectInfo, msgCount.containsKey(RocLogLevel.INFO));
                });
        RocLogger.setHandler(null);
    }

    @Test
    public void TestInvalidLoggerSetLevel() {
        assertThrows(IllegalArgumentException.class, () -> RocLogger.setLevel(null));
    }

    @Test
    public void TestLoggerSetNullHandler() {
        assertDoesNotThrow(() -> RocLogger.setHandler(null));
    }

    @Test
    public void TestValidLoggerSetHandler() throws Exception {
        Set<String> logs = Collections.newSetFromMap(new ConcurrentHashMap<>());
        RocLogger.setHandler((level, component, message) ->
                logs.add(String.format("[level=\"%s\", component=\"%s\"]: %s", level, component, message)));

        //noinspection EmptyTryBlock
        try (RocContext ignored = new RocContext()) {
        }

        String logOpen = "[level=\"INFO\", component=\"libroc\"]: roc_context_open";
        String logClose = "[level=\"INFO\", component=\"libroc\"]: roc_context_close";
        await().atMost(Duration.FIVE_MINUTES)
                .until(() -> {
                    boolean hasLogOpen = false, hasLogClose = false;
                    for (String log : logs) {
                        if (log.startsWith(logOpen)) {
                            hasLogOpen = true;
                        }
                        if (log.startsWith(logClose)) {
                            hasLogClose = true;
                        }
                    }
                    return hasLogOpen && hasLogClose;
                });
    }

    @Test
    public void TestInvalidLoggerNotThrows() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        assertDoesNotThrow(() -> {
            RocLogger.setHandler((level, component, message) -> {
                latch.countDown();
                throw new RuntimeException("Fails to log");
            });
            //noinspection EmptyTryBlock
            try (RocContext ignored = new RocContext()) {
            }
        });
        latch.await();
        RocLogger.setHandler(null);
    }
}
