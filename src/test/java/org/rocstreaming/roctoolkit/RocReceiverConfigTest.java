package org.rocstreaming.roctoolkit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RocReceiverConfigTest {

    private static RocReceiverConfig.ConfigBuilder validBuilder() {
        return RocReceiverConfig.builder()
                .frameSampleRate(44100)
                .frameChannels(ChannelSet.STEREO)
                .frameEncoding(FrameEncoding.PCM_FLOAT);
    }

    private static Stream<Arguments> testInvalidConfigArguments() {
        return Stream.of(
                Arguments.of("frameSampleRate must not be negative", validBuilder().frameSampleRate(-1)),
                Arguments.of("frameChannels must not be null", validBuilder().frameChannels(null)),
                Arguments.of("frameEncoding must not be null", validBuilder().frameEncoding(null))
        );
    }

    @ParameterizedTest()
    @MethodSource("testInvalidConfigArguments")
    public void testInvalidConfig(String error, RocReceiverConfig.ConfigBuilder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}