package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class LoggerTest {

    @Test
    public void TestValidLoggerSetLevel() {
        assertDoesNotThrow(() -> {
            Logger.setLevel(LogLevel.TRACE);
            Logger.setLevel(LogLevel.DEBUG);
            Logger.setLevel(LogLevel.INFO);
            Logger.setLevel(LogLevel.ERROR);
            Logger.setLevel(LogLevel.NONE);
        });
    }

    @Test
    public void TestInvalidLoggerSetLevel() {
        assertThrows(IllegalArgumentException.class, () -> {
            Logger.setLevel(null);
        });
    }

    @Test
    public void TestLoggerSetNullCallback() {
        assertDoesNotThrow(() -> {
            Logger.setCallback(null);
        });
    }

    @Test
    public void TestValidLoggerSetCallback() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        Logger.setLevel(LogLevel.INFO);
        Logger.setCallback((level, component, message) -> {
            System.out.println("[level=\"" + level + "\", component=\"" + component + "\"]: " + message);
        });

        try (
            Context c = new Context();
        ) {}
        System.out.flush();
        System.setOut(old);
        String[] lines = baos.toString().split(System.getProperty("line.separator"));
        String expected = String.join("\n",
                "[level=\"INFO\", component=\"libroc\"]: roc_context_open: opening context",
                "[level=\"INFO\", component=\"libroc\"]: roc_context_close: closed context");
        assertEquals(expected, String.join("\n", lines));
    }
}
