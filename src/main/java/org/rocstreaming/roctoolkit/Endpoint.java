package org.rocstreaming.roctoolkit;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Network endpoint.
 * <p>
 * Endpoint is a network entry point of a node. The definition includes the protocol being used,
 * network host and port, and, for some protocols, a resource. All these parts together are
 * unambiguously represented by a URI. The user may set or get the entire URI or its individual
 * parts.
 *
 *
 * <h2>Endpoint URI</h2>
 * <p>
 * Endpoint URI syntax is a subset of the syntax defined in RFC 3986: Examples:
 * <ul>
 *   <li>{@code rtsp://localhost:123/path?query}</li>
 *   <li>{@code rtp+rs8m://localhost:123}</li>
 *   <li>{@code rtp://127.0.0.1:123}</li>
 *   <li>{@code rtp://[::1]:123}</li>
 * </ul>
 * <p>
 * The following protocols (schemes) are supported:
 * <ul>
 *   <li>{@code rtp://} ( {@link Protocol#RTP} )</li>
 *   <li>{@code rtp+rs8m://} ( {@link Protocol#RTP_RS8M_SOURCE} )</li>
 *   <li>{@code rs8m://} ( {@link Protocol#RS8M_REPAIR} )</li>
 *   <li>{@code rtp+ldpc://} ( {@link Protocol#RTP_LDPC_SOURCE} )</li>
 *   <li>{@code ldpc://} ( {@link Protocol#LDPC_REPAIR} )</li>
 * </ul>
 * <p>
 * The host field should be either FQDN (domain name), or IPv4 address, or IPv6 address in square
 * brackets.
 * <p>
 * The port field can be omitted if the protocol defines standard port. Otherwise, the port can
 * not be omitted. For example, RTSP defines standard port, but RTP doesn't.
 * <p>
 * The path and query fields are allowed only for protocols that support them. For example,
 * they're supported by RTSP, but not by RTP.
 *
 *
 * <h2>Field invalidation</h2>
 * <p>
 * If some field is attempted to be set to an invalid value (for example, an invalid port
 * number), this specific field is marked as invalid until it is successfully set to some valid
 * value.
 * <p>
 * Sender and receiver refuse to bind or connect an endpoint which has invalid fields or doesn't
 * have some mandatory fields. Hence, it is safe to ignore errors returned by endpoint setters
 * and check only for errors returned by bind and connect operations.
 *
 *
 * <h2>Thread safety</h2>
 * <p>
 * Should not be used concurrently.
 */
@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
@EqualsAndHashCode
public class Endpoint {

    static {
        RocLibrary.loadLibrary();
    }

    /**
     * Protocol
     */
    private Protocol protocol;

    /**
     * Host specifies FQDN, IPv4 address, or IPv6 address
     */
    private String host;

    /**
     * Port specifies UDP or TCP port in range [0; 65535]
     * <p>
     * When binding an endpoint, the port may be set to zero to select a random port.
     * The selected port will be then written back to the endpoint. When connecting
     * an endpoint, the port should be positive.
     * <p>
     * If port is set to -1, the standard port for endpoint protocol is used. This is
     * allowed only if the protocol defines its standard port.
     */
    private int port;

    /**
     * Resource nullable. Specifies percent-encoded path and query
     */
    private String resource;

    /**
     * Create endpoint from uri
     *
     * @param uri uri
     */
    public Endpoint(String uri) {
        init(uri);
    }

    /**
     * Create endpoint from components
     *
     * @param protocol protocol
     * @param host     host specifies FQDN, IPv4 address, or IPv6 address
     * @param port     port specifies UDP or TCP port in range [0; 65535]
     *                 <p>
     *                 When binding an endpoint, the port may be set to zero to select a random port.
     *                 The selected port will be then written back to the endpoint. When connecting
     *                 an endpoint, the port should be positive.
     *                 <p>
     *                 If port is set to -1, the standard port for endpoint protocol is used. This is
     *                 allowed only if the protocol defines its standard port.
     * @param resource resource nullable. Specifies percent-encoded path and query
     */
    public Endpoint(Protocol protocol, String host, int port, String resource) {
        this.protocol = Check.notNull(protocol, "protocol");
        this.host = Check.notEmpty(host, "host");
        this.port = port;
        this.resource = resource;
        validate();
    }

    /**
     * Create endpoint from components
     *
     * @param protocol protocol
     * @param host     host specifies FQDN, IPv4 address, or IPv6 address
     * @param port     port specifies UDP or TCP port in range [0; 65535]
     *                 <p>
     *                 When binding an endpoint, the port may be set to zero to select a random port.
     *                 The selected port will be then written back to the endpoint. When connecting
     *                 an endpoint, the port should be positive.
     *                 <p>
     *                 If port is set to -1, the standard port for endpoint protocol is used. This is
     *                 allowed only if the protocol defines its standard port.
     */
    public Endpoint(Protocol protocol, String host, int port) {
        this(protocol, host, port, null);
    }

    @Override
    public String toString() {
        return getUri();
    }

    public native String getUri();

    private native void init(String uri) throws IllegalArgumentException;

    private native void validate() throws IllegalArgumentException;
}
