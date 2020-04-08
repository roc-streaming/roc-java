package com.github.rocproject.roc;

/**
 * Resampler profile.
 */
public enum ResamplerProfile {

    /**
     * No resampling.
     */
    ROC_RESAMPLER_DISABLE( getRocResamplerDisable() ),

    /**
     * Default profile.
     * Current default is {@link ResamplerProfile#ROC_RESAMPLER_MEDIUM ROC_RESAMPLER_MEDIUM}.
     */
    ROC_RESAMPLER_DEFAULT( getRocResamplerDefault() ),

    /**
     * High quality, low speed.
     */
    ROC_RESAMPLER_HIGH( getRocResamplerHigh() ),

    /**
     * Medium quality, medium speed.
     */
    ROC_RESAMPLER_MEDIUM( getRocResamplerMedium() ),

    /**
     * Low quality, high speed.
     */
    ROC_RESAMPLER_LOW( getRocResamplerLow() );

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
