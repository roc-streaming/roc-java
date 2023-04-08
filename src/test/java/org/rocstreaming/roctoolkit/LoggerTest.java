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

public class LoggerTest {

    @BeforeEach
    public void beforeEach() {
        Logger.setLevel(LogLevel.INFO);
    }

    private static Stream<Arguments> TestValidLoggerSetLevelProvider() {
        return Stream.of(
                Arguments.of(LogLevel.NONE, false, false),
                Arguments.of(LogLevel.ERROR, true, false),
                Arguments.of(LogLevel.INFO, true, true),
                Arguments.of(LogLevel.DEBUG, true, true),
                Arguments.of(LogLevel.TRACE, true, true)
        );
    }

    @ParameterizedTest
    @MethodSource("TestValidLoggerSetLevelProvider")
    public void TestValidLoggerSetLevel(LogLevel level, boolean expectError, boolean expectInfo) {
        Map<LogLevel, Integer> msgCount = new ConcurrentHashMap<>();
        LogHandler handler = (lvl, component, message) -> msgCount.compute(lvl, (k, v) -> v == null ? 1 : v + 1);
        Logger.setCallback(handler);

        assertDoesNotThrow(() -> {
            Logger.setLevel(level);
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
                    assertEquals(expectError, msgCount.containsKey(LogLevel.ERROR));
                    assertEquals(expectInfo, msgCount.containsKey(LogLevel.INFO));
                });
        Logger.setCallback(null);
    }

    @Test
    public void TestInvalidLoggerSetLevel() {
        assertThrows(IllegalArgumentException.class, () -> Logger.setLevel(null));
    }

    @Test
    public void TestLoggerSetNullCallback() {
        assertDoesNotThrow(() -> Logger.setCallback(null));
    }

    @Test
    public void TestValidLoggerSetCallback() throws Exception {
        Set<String> logs = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Logger.setCallback((level, component, message) ->
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
            Logger.setCallback((level, component, message) -> {
                latch.countDown();
                throw new RuntimeException("Fails to log");
            });
            //noinspection EmptyTryBlock
            try (RocContext ignored = new RocContext()) {
            }
        });
        latch.await();
        Logger.setCallback(null);
    }
}
