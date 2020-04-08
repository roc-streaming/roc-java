package com.github.rocproject.roc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddressTest {
    static {
        System.loadLibrary("roc_jni");
    }

    @Test
    public void IPv4AddressTest() {
        Address address = new Address(Family.ROC_AF_IPv4, "1.2.3.4", 123);
        assertEquals(Family.ROC_AF_IPv4, address.getFamily());
        assertEquals("1.2.3.4", address.getIp());
        assertEquals(123, address.getPort());
    }

    @Test
    public void IPv6AddressTest() {
        Address address = new Address(Family.ROC_AF_IPv6, "2001:db8::1", 123);
        assertEquals(Family.ROC_AF_IPv6, address.getFamily());
        assertEquals("2001:db8::1", address.getIp());
        assertEquals(123, address.getPort());
    }

    @Test
    public void DetectAddressTest() {
        assertEquals(Family.ROC_AF_IPv6, new Address(Family.ROC_AF_AUTO, "2001:db8::1", 123).getFamily());
        assertEquals(Family.ROC_AF_IPv4, new Address(Family.ROC_AF_AUTO, "1.2.3.4", 123).getFamily());
    }

    @Test
    public void BadArgsTest() {
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.ROC_AF_INVALID, "1.2.3.4", 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.ROC_AF_AUTO, null, 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.ROC_AF_AUTO, "bad", 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.ROC_AF_AUTO, "1.2.3.4", -1));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.ROC_AF_AUTO, "1.2.3.4", 65536));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.ROC_AF_IPv4, "2001:db8::1", 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.ROC_AF_IPv6, "1.2.3.4", 123));
    }
}
