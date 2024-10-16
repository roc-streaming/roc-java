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

    private static Stream<Arguments> invalidConfigArguments() {
        return Stream.of(
                Arguments.of("frameEncoding must not be null", validBuilder().frameEncoding(null)),
                Arguments.of("packetLength must not be negative", validBuilder().packetLength(Duration.ofNanos(-1))),
                Arguments.of("fecBlockSourcePackets must not be negative", validBuilder().fecBlockSourcePackets(-1)),
                Arguments.of("fecBlockRepairPackets must not be negative", validBuilder().fecBlockRepairPackets(-1))
        );
    }

    @ParameterizedTest()
    @MethodSource("invalidConfigArguments")
    public void testInvalidConfig(String error, RocSenderConfig.Builder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}
