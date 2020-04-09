package com.github.rocproject.roc;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class LoggerTest {

    @Test
    public void TestValidLoggerSetLevel() {
        assertDoesNotThrow(() -> {
            Logger.setLevel(LogLevel.NONE);
            Logger.setLevel(LogLevel.ERROR);
            Logger.setLevel(LogLevel.INFO);
            Logger.setLevel(LogLevel.DEBUG);
            Logger.setLevel(LogLevel.TRACE);
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

            Logger.setLevel(LogLevel.INFO);
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
            assertEquals("[level=\"INFO\", component=\"roc_lib\"]: \"roc_context: opening context\"", lines[0]);
            assertEquals("[level=\"INFO\", component=\"roc_lib\"]: \"roc_context: closed context\"", lines[1]);
        });
    }

    @Test
    public void TestInvalidLoggerSetCallback() {
        assertThrows(IllegalArgumentException.class, () -> {
            Logger.setCallback(null);
        });
    }
}
