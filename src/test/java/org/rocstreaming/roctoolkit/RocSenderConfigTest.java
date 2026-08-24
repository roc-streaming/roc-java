package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RocSenderConfigTest {

    private static RocSenderConfig.Builder validBuilder() {
        return RocSenderConfig.builder()
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

    @Test
    public void testNativeEnumValues() {
        assertEquals(0, ClockSource.DEFAULT.value);
        assertEquals(1, ClockSource.EXTERNAL.value);
        assertEquals(2, ClockSource.INTERNAL.value);

        assertEquals(0, LatencyTunerBackend.DEFAULT.value);
        assertEquals(2, LatencyTunerBackend.NIQ.value);

        assertEquals(0, LatencyTunerProfile.DEFAULT.value);
        assertEquals(1, LatencyTunerProfile.INTACT.value);
        assertEquals(2, LatencyTunerProfile.RESPONSIVE.value);
        assertEquals(3, LatencyTunerProfile.GRADUAL.value);
    }

    private static Stream<Arguments> invalidConfigArguments() {
        return Stream.of(
                Arguments.of("Invalid RocSenderConfig.frameEncoding: must not be null", validBuilder().frameEncoding(null)),
                Arguments.of("Invalid RocSenderConfig.packetLength: must not be negative", validBuilder().packetLength(Duration.ofNanos(-1))),
                Arguments.of("Invalid RocSenderConfig.fecBlockSourcePackets: must not be negative", validBuilder().fecBlockSourcePackets(-1)),
                Arguments.of("Invalid RocSenderConfig.fecBlockRepairPackets: must not be negative", validBuilder().fecBlockRepairPackets(-1)),
                Arguments.of("Invalid RocSenderConfig.targetLatency: must not be negative", validBuilder().targetLatency(Duration.ofNanos(-1))),
                Arguments.of("Invalid RocSenderConfig.latencyTolerance: must not be negative", validBuilder().latencyTolerance(Duration.ofNanos(-1)))
        );
    }

    @ParameterizedTest()
    @MethodSource("invalidConfigArguments")
    public void testInvalidConfig(String error, RocSenderConfig.Builder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}
