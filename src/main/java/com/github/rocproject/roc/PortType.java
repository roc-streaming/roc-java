package com.github.rocproject.roc;

/**
 * Network port type.
 */
public enum PortType {

    /**
     * Network port for audio source packets.
     * If FEC is not used, this type of port is used to send or receive audio packets.
     * If FEC is used, this type of port is used to send or receive FEC source packets
     * containing audio data plus some FEC headers.
     */
    AUDIO_SOURCE( getRocPortAudioSource() ),

    /**
     * Network port for audio repair packets.
     * If FEC is used, this type of port is used to send or receive FEC repair packets
     * containing redundant data for audio plus some FEC headers.
     */
    AUDIO_REPAIR( getRocPortAudioRepair() );

    private final int value;
    PortType(int newValue ) {
        this.value = newValue;
    }
    public int getValue() {
        return( this.value );
    }

    private static native int getRocPortAudioSource();
    private static native int getRocPortAudioRepair();
}
