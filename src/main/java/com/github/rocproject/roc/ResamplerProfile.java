package com.github.rocproject.roc;

import java.util.function.Supplier;

/**
 * Resampler profile.
 */
public enum ResamplerProfile {

    /**
     * No resampling.
     */
    DISABLE( ResamplerProfile::getRocResamplerDisable ),

    /**
     * Default profile.
     * Current default is {@link ResamplerProfile#MEDIUM MEDIUM}.
     */
    DEFAULT( ResamplerProfile::getRocResamplerDefault ),

    /**
     * High quality, low speed.
     */
    HIGH( ResamplerProfile::getRocResamplerHigh ),

    /**
     * Medium quality, medium speed.
     */
    MEDIUM( ResamplerProfile::getRocResamplerMedium ),

    /**
     * Low quality, high speed.
     */
    LOW( ResamplerProfile::getRocResamplerLow );

    private final int value;
    ResamplerProfile(Supplier<Integer> value) {
        RocLibrary.loadLibrary();
        this.value = value.get();
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocResamplerDisable();
    private static native int getRocResamplerDefault();
    private static native int getRocResamplerHigh();
    private static native int getRocResamplerMedium();
    private static native int getRocResamplerLow();
}
