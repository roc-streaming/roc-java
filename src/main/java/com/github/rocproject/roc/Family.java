package com.github.rocproject.roc;

/**
 *  Network address family.
 */
public enum Family {

    /**
     * Invalid address.
     */
    INVALID( getRocAFInvalid() ),

    /**
     * Automatically detect address family from string format.
     */
    AUTO( getRocAFAuto() ),

    /**
     * IPv4 address.
     */
    IPv4( getRocAFIPv4() ),

    /**
     * IPv6 address.
     */
    IPv6( getRocAFIPv6() );

    private final int value;
    Family(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocAFInvalid();
    private static native int getRocAFAuto();
    private static native int getRocAFIPv4();
    private static native int getRocAFIPv6();
}
