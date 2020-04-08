package com.github.rocproject.roc;

/**
 * Resampler profile.
 */
public enum ResamplerProfile {

    /**
     * No resampling.
     */
    DISABLE( getRocResamplerDisable() ),

    /**
     * Default profile.
     * Current default is {@link ResamplerProfile#MEDIUM MEDIUM}.
     */
    DEFAULT( getRocResamplerDefault() ),

    /**
     * High quality, low speed.
     */
    HIGH( getRocResamplerHigh() ),

    /**
     * Medium quality, medium speed.
     */
    MEDIUM( getRocResamplerMedium() ),

    /**
     * Low quality, high speed.
     */
    LOW( getRocResamplerLow() );

    private final int value;
    ResamplerProfile(int newValue ) {
        this.value = newValue;
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
