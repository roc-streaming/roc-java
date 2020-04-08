package com.github.rocproject.roc;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class LoggerTest {
    static {
        System.loadLibrary("native");
    }

    @Test
    public void TestValidLoggerSetLevel() {
        assertDoesNotThrow(() -> {
            Logger.setLevel(LogLevel.ROC_LOG_NONE);
            Logger.setLevel(LogLevel.ROC_LOG_ERROR);
            Logger.setLevel(LogLevel.ROC_LOG_INFO);
            Logger.setLevel(LogLevel.ROC_LOG_DEBUG);
            Logger.setLevel(LogLevel.ROC_LOG_TRACE);
        });
    }

    @Test
    public void TestInvalidLoggerSetLevel() {
        assertThrows(IllegalArgumentException.class, () -> {
            Logger.setLevel(null);
        });
    }

    @Test
    public void TestValidLoggerSetCallback() {
        assertDoesNotThrow(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            Logger.setLevel(LogLevel.ROC_LOG_INFO);
            Logger.setCallback((level, component, message) -> {
                System.out.println("[level=\"" + level + "\", component=\"" + component + "\"]: \"" + message + "\"");
            });

            try (
                Context c = new Context();
            ) {}
            System.out.flush();
            System.setOut(old);
            String[] lines = baos.toString().split(System.getProperty("line.separator"));
            assertEquals(2, lines.length);
            assertEquals("[level=\"ROC_LOG_INFO\", component=\"roc_lib\"]: \"roc_context: opening context\"", lines[0]);
            assertEquals("[level=\"ROC_LOG_INFO\", component=\"roc_lib\"]: \"roc_context: closed context\"", lines[1]);
        });
    }

    @Test
    public void TestInvalidLoggerSetCallback() {
        assertThrows(IllegalArgumentException.class, () -> {
            Logger.setCallback(null);
        });
    }
}
