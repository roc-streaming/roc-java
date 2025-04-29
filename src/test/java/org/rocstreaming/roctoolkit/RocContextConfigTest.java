package org.rocstreaming.roctoolkit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RocContextConfigTest {

    private static Stream<Arguments> testInvalidConfigArguments() {
        return Stream.of(
                Arguments.of("maxFrameSize must not be negative", RocContextConfig.builder().maxFrameSize(-1)),
                Arguments.of("maxPacketSize must not be negative", RocContextConfig.builder().maxPacketSize(-1))
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidConfigArguments")
    public void testInvalidConfig(String error, RocContextConfig.Builder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}
