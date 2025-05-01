package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MediaEncodingTest {

    private static MediaEncoding.Builder validBuilder() {
        return MediaEncoding.builder()
                .rate(44100)
                .format(Format.PCM_FLOAT32)
                .channels(ChannelLayout.STEREO);
    }

    @Test
    public void testValidEncoding() {
        assertDoesNotThrow(() -> validBuilder().build());
    }

    private static Stream<Arguments> invalidEncodingArguments() {
        return Stream.of(
                Arguments.of("Invalid MediaEncoding.rate: must not be negative", validBuilder().rate(-1)),
                Arguments.of("Invalid MediaEncoding.format: must not be null", validBuilder().format(null)),
                Arguments.of("Invalid MediaEncoding.channels: must not be null", validBuilder().channels(null)),
                Arguments.of("Invalid MediaEncoding: when 'channels' is MULTITRACK, 'tracks' must be in range [1; 1024]",
                        validBuilder().channels(ChannelLayout.MULTITRACK).tracks(0)),
                Arguments.of("Invalid MediaEncoding: when 'channels' is MULTITRACK, 'tracks' must be in range [1; 1024]",
                        validBuilder().channels(ChannelLayout.MULTITRACK).tracks(1025)),
                Arguments.of("Invalid MediaEncoding: when 'channels' isn't MULTITRACK, 'tracks' must be zero",
                        validBuilder().channels(ChannelLayout.STEREO).tracks(1))
        );
    }

    @ParameterizedTest()
    @MethodSource("invalidEncodingArguments")
    public void testInvalidEncoding(String error, MediaEncoding.Builder builder) {
        Exception e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals(error, e.getMessage());
    }
}
