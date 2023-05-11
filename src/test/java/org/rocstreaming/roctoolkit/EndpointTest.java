package org.rocstreaming.roctoolkit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EndpointTest extends BaseTest {

    static class Params {
        String name;
        String uri;
        Class<? extends Throwable> uriException;
        Protocol protocol;
        String host;
        int port;
        String resource;
        Class<? extends Throwable> componentsException;
        String componentsExceptionMsg;

        @Override
        public String toString() {
            return name;
        }
    }

    public static List<Params> endpointsSource() {
        ArrayList<Params> result = new ArrayList<>();
        // protocols
        {
            Params params = new Params();
            params.name = "rtsp protocol";
            params.uri = "rtsp://192.168.0.1:12345/path?query1=query1&query2=query2";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = 12345;
            params.resource = "/path?query1=query1&query2=query2";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "rtp protocol";
            params.uri = "rtp://192.168.0.1:12345";
            params.protocol = Protocol.RTP;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "rtp+rs8m protocol";
            params.uri = "rtp+rs8m://192.168.0.1:12345";
            params.protocol = Protocol.RTP_RS8M_SOURCE;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "rs8m protocol";
            params.uri = "rs8m://192.168.0.1:12345";
            params.protocol = Protocol.RS8M_REPAIR;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "rtp+ldpc protocol";
            params.uri = "rtp+ldpc://192.168.0.1:12345";
            params.protocol = Protocol.RTP_LDPC_SOURCE;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "ldpc protocol";
            params.uri = "ldpc://192.168.0.1:12345";
            params.protocol = Protocol.LDPC_REPAIR;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "rtcp protocol";
            params.uri = "rtcp://192.168.0.1:12345";
            params.protocol = Protocol.RTCP;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        // components
        {
            Params params = new Params();
            params.name = "use default rtsp port";
            params.uri = "rtsp://192.168.0.1";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = -1;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "use default rtsp port";
            params.uri = "rtsp://192.168.0.1:12345";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "rtsp without resource";
            params.uri = "rtsp://192.168.0.1:12345";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = 12345;
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "rtsp without query params";
            params.uri = "rtsp://192.168.0.1:12345/path";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = 12345;
            params.resource = "/path";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "use zero port (for bind)";
            params.uri = "rtsp://192.168.0.1:0";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = 0;
            result.add(params);
        }
        // errors
        {
            Params params = new Params();
            params.name = "empty uri";
            params.uri = "";
            params.uriException = IllegalArgumentException.class;
            params.componentsException = IllegalArgumentException.class;
            params.componentsExceptionMsg = "protocol must not be null";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "missing host and port";
            params.uri = "rtsp://";
            params.protocol = Protocol.RTSP;
            params.uriException = IllegalArgumentException.class;
            params.componentsException = IllegalArgumentException.class;
            params.componentsExceptionMsg = "host must not be empty";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "missing host";
            params.uri = "rtsp://:12345";
            params.protocol = Protocol.RTSP;
            params.port = 12345;
            params.uriException = IllegalArgumentException.class;
            params.componentsException = IllegalArgumentException.class;
            params.componentsExceptionMsg = "host must not be empty";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "empty host";
            params.uri = "rtsp://:12345";
            params.protocol = Protocol.RTSP;
            params.host = "";
            params.port = 12345;
            params.uriException = IllegalArgumentException.class;
            params.componentsException = IllegalArgumentException.class;
            params.componentsExceptionMsg = "host must not be empty";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "port out of range";
            params.uri = "rtsp://192.168.0.1:65536";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = 655356;
            params.uriException = IllegalArgumentException.class;
            params.componentsException = Exception.class;
            params.componentsExceptionMsg = "Invalid roc_endpoint";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "port out of range - negative";
            params.uri = "rtsp://192.168.0.1:-2";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = -2;
            params.uriException = IllegalArgumentException.class;
            params.componentsException = Exception.class;
            params.componentsExceptionMsg = "Invalid roc_endpoint";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "invalid resource";
            params.uri = "rtsp://192.168.0.1/??";
            params.protocol = Protocol.RTSP;
            params.host = "192.168.0.1";
            params.port = -1;
            params.resource = "??";
            params.uriException = IllegalArgumentException.class;
            params.componentsException = Exception.class;
            params.componentsExceptionMsg = "Invalid roc_endpoint";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "resource not allowed for protocol";
            params.uri = "rtp://192.168.0.1:12345/path";
            params.protocol = Protocol.RTP;
            params.host = "192.168.0.1";
            params.port = 12345;
            params.resource = "/path";
            params.uriException = IllegalArgumentException.class;
            params.componentsException = Exception.class;
            params.componentsExceptionMsg = "Invalid roc_endpoint";
            result.add(params);
        }
        {
            Params params = new Params();
            params.name = "default port not defined for protocol";
            params.uri = "rtp://192.168.0.1";
            params.protocol = Protocol.RTP;
            params.host = "192.168.0.1";
            params.port = -1;
            params.uriException = IllegalArgumentException.class;
            params.componentsException = Exception.class;
            params.componentsExceptionMsg = "Invalid roc_endpoint";
            result.add(params);
        }
        return result;
    }

    @MethodSource("endpointsSource")
    @ParameterizedTest(name = "{0}")
    public void testEndpointUri(Params params) {
        if (params.uriException != null) {
            assertThrows(params.uriException, () -> new Endpoint(params.uri));
            return;
        }
        Endpoint endpoint = new Endpoint(params.uri);
        assertEquals(params.protocol, endpoint.getProtocol());
        assertEquals(params.host, endpoint.getHost());
        assertEquals(params.port, endpoint.getPort());
        assertEquals(params.resource, endpoint.getResource());
        assertEquals(params.uri, endpoint.getUri());
    }

    @MethodSource("endpointsSource")
    @ParameterizedTest(name = "{0}")
    public void testEndpointComponents(Params params) {
        if (params.componentsException != null) {
            Throwable e = assertThrows(
                    params.componentsException,
                    () -> new Endpoint(params.protocol, params.host, params.port, params.resource)
            );
            assertEquals(params.componentsExceptionMsg, e.getMessage());
            return;
        }
        Endpoint endpoint = new Endpoint(params.protocol, params.host, params.port, params.resource);
        assertEquals(params.protocol, endpoint.getProtocol());
        assertEquals(params.host, endpoint.getHost());
        assertEquals(params.port, endpoint.getPort());
        assertEquals(params.resource, endpoint.getResource());
        assertEquals(params.uri, endpoint.getUri());
    }

    @MethodSource("endpointsSource")
    @ParameterizedTest(name = "{0}")
    public void testEndpointBuilder(Params params) {
        if (params.componentsException != null) {
            assertThrows(params.componentsException, () -> new Endpoint.Builder()
                    .setProtocol(params.protocol)
                    .setHost(params.host)
                    .setPort(params.port)
                    .setResource(params.resource)
                    .build());
            return;
        }
        Endpoint endpoint = new Endpoint.Builder()
                .setProtocol(params.protocol)
                .setHost(params.host)
                .setPort(params.port)
                .setResource(params.resource)
                .build();
        assertEquals(params.protocol, endpoint.getProtocol());
        assertEquals(params.host, endpoint.getHost());
        assertEquals(params.port, endpoint.getPort());
        assertEquals(params.resource, endpoint.getResource());
        assertEquals(params.uri, endpoint.getUri());
    }

    @Test
    public void testInvalidEndpointBuilder() {
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().build());
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().setHost("host").build());
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().setHost("host").setPort(1).build());
        assertThrows(IllegalArgumentException.class, () -> new Endpoint.Builder().setHost("host").setResource("/resource").build());
    }

    @Test
    public void testEndpointMinimalConstructor() {
        Endpoint endpoint = new Endpoint(Protocol.RTP, "192.168.0.1", 12345);
        assertEquals(Protocol.RTP, endpoint.getProtocol());
        assertEquals("192.168.0.1", endpoint.getHost());
        assertEquals(12345, endpoint.getPort());
        assertNull(endpoint.getResource());
        assertEquals("rtp://192.168.0.1:12345", endpoint.getUri());
    }
}