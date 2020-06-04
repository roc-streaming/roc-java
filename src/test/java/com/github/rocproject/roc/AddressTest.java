package com.github.rocproject.roc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;

public class AddressTest {

    @BeforeAll
    public static void beforeAll() {
        Logger.setLevel(LogLevel.NONE);
    }

    @Test
    public void IPv4AddressTest() {
        Address address = new Address(Family.IPv4, "1.2.3.4", 123);
        assertEquals(Family.IPv4, address.getFamily());
        assertEquals("1.2.3.4", address.getIp());
        assertEquals(123, address.getPort());
    }

    @Test
    public void IPv6AddressTest() {
        Address address = new Address(Family.IPv6, "2001:db8::1", 123);
        assertEquals(Family.IPv6, address.getFamily());
        assertEquals("2001:db8::1", address.getIp());
        assertEquals(123, address.getPort());
    }

    @Test
    public void DetectAddressTest() {
        assertEquals(Family.IPv6, new Address(Family.AUTO, "2001:db8::1", 123).getFamily());
        assertEquals(Family.IPv4, new Address(Family.AUTO, "1.2.3.4", 123).getFamily());
    }

    @Test
    public void BadArgsTest() {
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.INVALID, "1.2.3.4", 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.AUTO, null, 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.AUTO, "bad", 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.AUTO, "1.2.3.4", -1));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.AUTO, "1.2.3.4", 65536));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.IPv4, "2001:db8::1", 123));
        assertThrows(IllegalArgumentException.class, () -> new Address(Family.IPv6, "1.2.3.4", 123));
    }
}
