package org.rocstreaming.roctoolkit;

import lombok.NoArgsConstructor;

@SuppressWarnings("unused") // used by JNI
@NoArgsConstructor
class ProtocolUtils {

    private static Protocol getByValue(int value) {
        for (Protocol protocol : Protocol.values()) {
            if (value == protocol.value) {
                return protocol;
            }
        }
        return null;
    }
}
