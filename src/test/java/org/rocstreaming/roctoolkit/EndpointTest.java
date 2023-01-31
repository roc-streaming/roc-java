package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class EndpointTest {

    @BeforeAll
    public static void beforeAll() {
        Logger.setLevel(LogLevel.ERROR);
    }

    @Test
    public void EndpointCreateUriTest() {
        Endpoint endpoint = new Endpoint("rtsp://127.0.0.1:10001/path");
        assertEquals(Protocol.RTSP, endpoint.getProtocol());
        assertEquals("127.0.0.1", endpoint.getHost());
        assertEquals(10001, endpoint.getPort());
        assertEquals("/path", endpoint.getResource());
        assertEquals("rtsp://127.0.0.1:10001/path", endpoint.getUri());
    }

    @Test
    public void EndpointCreateUriMinimumTest() {
        Endpoint endpoint = new Endpoint("rtsp://127.0.0.1");
        assertEquals(Protocol.RTSP, endpoint.getProtocol());
        assertEquals(-1, endpoint.getPort());
        assertNull(endpoint.getResource());
        assertEquals("rtsp://127.0.0.1", endpoint.getUri());
    }

    @Test
    public void EndpointCreateComponentsTest() {
        Endpoint endpoint = new Endpoint(Protocol.RTSP, "127.0.0.1", 10001, "/path");
        assertEquals(Protocol.RTSP, endpoint.getProtocol());
        assertEquals("127.0.0.1", endpoint.getHost());
        assertEquals(10001, endpoint.getPort());
        assertEquals("/path", endpoint.getResource());
        assertEquals("rtsp://127.0.0.1:10001/path", endpoint.getUri());
    }

    @Test
    public void EndpointInvalidTest() {
        assertThrows(IllegalArgumentException.class, () -> new Endpoint(null));
        assertThrows(IllegalArgumentException.class, () -> new Endpoint(""));
        assertThrows(IllegalArgumentException.class, () -> new Endpoint("rtsp://"));
        assertThrows(IllegalArgumentException.class, () -> new Endpoint("rtsp://:12345"));
        assertThrows(IllegalArgumentException.class, () -> new Endpoint("rt://0.0.0.0"));
        assertThrows(IllegalArgumentException.class, () -> new Endpoint("rt://0.0.0.0:10001"));
        assertThrows(IllegalArgumentException.class, () -> new Endpoint("0.0.0.0:10001"));
    }

    @Test
    public void EndpointBuilderTest() {
        Endpoint endpoint = new Endpoint.Builder()
                .setProtocol(Protocol.RTSP)
                .setHost("127.0.0.1")
                .setPort(10001)
                .setResource("/path")
                .build();

        assertEquals(Protocol.RTSP, endpoint.getProtocol());
        assertEquals("127.0.0.1", endpoint.getHost());
        assertEquals(10001, endpoint.getPort());
        assertEquals("/path", endpoint.getResource());
        assertEquals("rtsp://127.0.0.1:10001/path", endpoint.getUri());
    }

    @Test
    public void EndpointBuilderInvalidTest() {
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().build());
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().setHost("host").build());
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().setHost("host").setPort(1).build());
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().setHost("host").setResource("/resource").build());
    }
}