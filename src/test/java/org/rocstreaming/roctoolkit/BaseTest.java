package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class BaseTest {

    static private Level originalLogLevel;

    @BeforeAll
    static void configureLogger() throws Exception {
        try (InputStream is = RocReceiverTest.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }

        originalLogLevel = RocLogger.LOGGER.getLevel();

        String gradleLogLevel = System.getProperty("org.gradle.logging.level");

        if (gradleLogLevel == null || gradleLogLevel.equals("INFO") || gradleLogLevel.equals("DEBUG")) {
            RocLogger.LOGGER.setLevel(Level.FINE);
        } else {
            RocLogger.LOGGER.setLevel(Level.OFF);
        }
    }

    @AfterAll
    static public void restoreLogger() {
        RocLogger.LOGGER.setLevel(originalLogLevel);
    }
}
