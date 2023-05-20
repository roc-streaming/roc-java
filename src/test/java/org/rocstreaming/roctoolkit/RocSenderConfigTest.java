package org.rocstreaming.roctoolkit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RocSenderConfigTest {


    private static RocSenderConfig.ConfigBuilder validBuilder() {
        return RocSenderConfig.builder()
                .frameSampleRate(44100)
                .frameChannels(ChannelSet.STEREO)
                .frameEncoding(FrameEncoding.PCM_FLOAT);
    }

    private static Stream<Arguments> testInvalidConfigArguments() {
        return Stream.of(
                Arguments.of("frameSampleRate must not be negative", validBuilder().frameSampleRate(-1)),
                Arguments.of("frameChannels must not be null", validBuilder().frameChannels(null)),
                Arguments.of("frameEncoding must not be null", validBuilder().frameEncoding(null)),
                Arguments.of("packetSampleRate must not be negative", validBuilder().packetSampleRate(-1)),
                Arguments.of("fecBlockSourcePackets must not be negative", validBuilder().fecBlockSourcePackets(-1)),
                Arguments.of("fecBlockRepairPackets must not be negative", validBuilder().fecBlockRepairPackets(-1))
        );
    }

    @ParameterizedTest()
    @MethodSource("testInvalidConfigArguments")
    public void testInvalidConfig(String error, RocSenderConfig.ConfigBuilder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }

}