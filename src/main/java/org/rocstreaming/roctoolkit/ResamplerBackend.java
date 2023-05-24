package org.rocstreaming.roctoolkit;

/**
 * Resampler backend.
 * Affects speed and quality.
 * Some backends may be disabled at build time.
 */
public enum ResamplerBackend {
    /**
     * Default backend.
     * Depends on what was enabled at build time.
     */
    DEFAULT(0),

    /**
     * Slow built-in resampler.
     * Always available.
     */
    BUILTIN(1),

    /**
     * Fast good-quality resampler from SpeexDSP.
     * May be disabled at build time.
     */
    SPEEX(2);

    final int value;

    ResamplerBackend(int value) {
        this.value = value;
    }
}
