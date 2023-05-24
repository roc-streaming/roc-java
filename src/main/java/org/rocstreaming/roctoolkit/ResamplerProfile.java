package org.rocstreaming.roctoolkit;

/**
 * Resampler profile.
 */
public enum ResamplerProfile {

    /**
     * No resampling.
     */
    DISABLE(-1),

    /**
     * Default profile.
     * Current default is {@link ResamplerProfile#MEDIUM MEDIUM}.
     */
    DEFAULT(0),

    /**
     * High quality, low speed.
     */
    HIGH(1),

    /**
     * Medium quality, medium speed.
     */
    MEDIUM(2),

    /**
     * Low quality, high speed.
     */
    LOW(3);

    final int value;

    ResamplerProfile(int value) {
        this.value = value;
    }
}
