package com.github.rocproject.roc;

import java.util.function.Supplier;

/**
 *  Network address family.
 */
public enum Family {

    /**
     * Invalid address.
     */
    INVALID( Family::getRocAFInvalid ),

    /**
     * Automatically detect address family from string format.
     */
    AUTO( Family::getRocAFAuto ),

    /**
     * IPv4 address.
     */
    IPv4( Family::getRocAFIPv4 ),

    /**
     * IPv6 address.
     */
    IPv6( Family::getRocAFIPv6 );

    private final int value;
    Family(Supplier<Integer> value) {
        RocLibrary.loadLibrary();
        this.value = value.get();
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocAFInvalid();
    private static native int getRocAFAuto();
    private static native int getRocAFIPv4();
    private static native int getRocAFIPv6();
}
