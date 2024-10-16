package org.rocstreaming.roctoolkit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RocReceiverConfigTest {

    private static RocReceiverConfig.Builder validBuilder() {
        return RocReceiverConfig.builder()
                .frameSampleRate(44100)
                .frameChannels(ChannelSet.STEREO)
                .frameEncoding(FrameEncoding.PCM_FLOAT);
    }

    private static Stream<Arguments> testInvalidConfigArguments() {
        return Stream.of(
                Arguments.of("frameSampleRate must not be negative", validBuilder().frameSampleRate(-1)),
                Arguments.of("frameChannels must not be null", validBuilder().frameChannels(null)),
                Arguments.of("frameEncoding must not be null", validBuilder().frameEncoding(null)),
                Arguments.of("targetLatency must not be negative", validBuilder().targetLatency(Duration.ofNanos(-1))),
                Arguments.of("maxLatencyOverrun must not be negative", validBuilder().maxLatencyOverrun(Duration.ofNanos(-1))),
                Arguments.of("maxLatencyUnderrun must not be negative", validBuilder().maxLatencyUnderrun(Duration.ofNanos(-1))),
                Arguments.of("breakageDetectionWindow must not be negative", validBuilder().breakageDetectionWindow(Duration.ofNanos(-1)))
        );
    }

    @ParameterizedTest()
    @MethodSource("testInvalidConfigArguments")
    public void testInvalidConfig(String error, RocReceiverConfig.Builder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}
