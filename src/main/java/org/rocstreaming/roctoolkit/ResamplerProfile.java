// DO NOT EDIT! Code generated by generate_enums script from roc-toolkit
// roc-toolkit git tag: v0.2.5-11-g14d642e9, commit: 14d642e9

package org.rocstreaming.roctoolkit;

/**
 * Resampler profile.
 * <p>
 * Affects speed and quality. Each resampler backend treats profile in its own
 * way.
 */
public enum ResamplerProfile {

    /**
     * Do not perform resampling.
     * <p>
     * Clock drift compensation will be disabled in this case. If in doubt, do
     * not disable resampling.
     */
    DISABLE(-1),

    /**
     * Default profile.
     * <p>
     * Current default is {@link ResamplerProfile#MEDIUM}.
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
    LOW(3),
    ;

    final int value;

    ResamplerProfile(int value) {
        this.value = value;
    }
}
