package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RocContextConfigTest {

    private static RocContextConfig.Builder validBuilder() {
        return RocContextConfig.builder();
    }

    @Test
    public void testValidConfig() {
        assertDoesNotThrow(() -> validBuilder().build());
    }

    private static Stream<Arguments> invalidConfigArguments() {
        return Stream.of(
                Arguments.of("Invalid RocContextConfig.maxFrameSize: must not be negative", validBuilder().maxFrameSize(-1)),
                Arguments.of("Invalid RocContextConfig.maxPacketSize: must not be negative", validBuilder().maxPacketSize(-1))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidConfigArguments")
    public void testInvalidConfig(String error, RocContextConfig.Builder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}
