package org.rocstreaming.roctoolkit;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoggerTest {

    @BeforeEach
    public void beforeEach() {
        Logger.setLevel(LogLevel.INFO);
    }

    @ParameterizedTest
    @EnumSource(LogLevel.class)
    public void TestValidLoggerSetLevel(LogLevel level) {
        assertDoesNotThrow(() -> {
            Logger.setLevel(level);
            try {
                // trigger error logs
                new Endpoint("invalid");
            } catch (Exception ignored) {
            }
            // trigger info logs
            //noinspection EmptyTryBlock
            try (Context ignored = new Context()) {
            }
        });
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
        try (Context ignored = new Context()) {
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
    public void TestInvalidLoggerNotThrows() {
        assertDoesNotThrow(() -> {
            Logger.setCallback((level, component, message) -> {
                throw new RuntimeException("Fails to log");
            });
            //noinspection EmptyTryBlock
            try (Context ignored = new Context()) {
            }
        });
        Logger.setCallback(null);
    }
}
