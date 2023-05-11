package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class BaseTest {

    @BeforeAll
    static void configureLogger() throws IOException {
        try (InputStream is = RocReceiverTest.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }
}
