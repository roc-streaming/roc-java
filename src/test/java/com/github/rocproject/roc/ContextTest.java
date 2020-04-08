package com.github.rocproject.roc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ContextTest {
    static {
        System.loadLibrary("roc_jni");
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
            Context context = new Context(new ContextConfig.Builder().maxPacketSize(-1).maxFrameSize(-1).build());
            context.close();
        });
    }
}
