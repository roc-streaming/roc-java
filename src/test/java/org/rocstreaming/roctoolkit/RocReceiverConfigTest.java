package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RocReceiverConfigTest {

    private static RocReceiverConfig.Builder validBuilder() {
        return RocReceiverConfig.builder()
                .frameEncoding(
                        MediaEncoding.builder()
                                .rate(44100)
                                .format(Format.PCM_FLOAT32)
                                .channels(ChannelLayout.STEREO)
                                .build()
                );
    }

    @Test
    public void testValidConfig() {
        assertDoesNotThrow(() -> validBuilder().build());
    }

    private static Stream<Arguments> invalidConfigArguments() {
        return Stream.of(
                Arguments.of("Invalid RocReceiverConfig.frameEncoding: must not be null", validBuilder().frameEncoding(null)),
                Arguments.of("Invalid RocReceiverConfig.targetLatency: must not be negative", validBuilder().targetLatency(Duration.ofNanos(-1))),
                Arguments.of("Invalid RocReceiverConfig.latencyTolerance: must not be negative", validBuilder().latencyTolerance(Duration.ofNanos(-1)))
        );
    }

    @ParameterizedTest()
    @MethodSource("invalidConfigArguments")
    public void testInvalidConfig(String error, RocReceiverConfig.Builder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}
