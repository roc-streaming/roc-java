package com.rocproject.roc.context;

import org.junit.jupiter.api.Test;
import com.rocproject.roc.config.ContextConfig;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ContextTest {
    static {
        System.loadLibrary("native");
    }

    @Test
    public void ContextDefaultConfigTest() {
        assertDoesNotThrow(() -> {
            Context context = new Context();
            context.close();
        });
    }

    @Test
    public void ContextWithInvalidConfigTest() {
        assertThrows(Exception.class, () -> {
            Context context = new Context(new ContextConfig(-1, -1));
            context.close();
        });
    }
}
