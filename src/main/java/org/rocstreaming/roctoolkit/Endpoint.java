package org.rocstreaming.roctoolkit;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Network endpoint.
 * <p>
 * Endpoint is a network entry point of a peer. The definition includes the protocol being used,
 * network host and port, and, for some protocols, a resource. All these parts together are
 * unambiguously represented by a URI. The user may set or get the entire URI or its individual
 * parts.
 * <p>
 * <b>Endpoint URI</b>
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
 * <p>
 * <b>Field invalidation</b>
 * <p>
 * If some field is attempted to be set to an invalid value (for example, an invalid port
 * number), this specific field is marked as invalid until it is successfully set to some valid
 * value.
 * <p>
 * Sender and receiver refuse to bind or connect an endpoint which has invalid fields or doesn't
 * have some mandatory fields. Hence, it is safe to ignore errors returned by endpoint setters
 * and check only for errors returned by bind and connect operations.
 * <p>
 * <b>Thread safety</b>
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
     * Set endpoint protocol (scheme).
     * <p>
     * On failure, invalidates endpoint protocol. The endpoint becomes invalid until its
     * protocol is successfully set.
     * <p>
     * <b>Parameters</b>
     * <ul>
     *   <li>{@code endpoint} should point to initialized endpoint</li>
     *   <li>{@code proto} defines new protocol</li>
     * </ul>
     * <p>
     * <b>Returns</b>
     * <ul>
     *   <li>returns zero if protocol was successfully set</li>
     *   <li>returns a negative value on invalid arguments</li>
     *   <li>returns a negative value if protocol is incompatible with other URI parameters</li>
     * </ul>
     */
    private Protocol protocol;

    /**
     * Set endpoint host.
     * <p>
     * On failure, invalidates endpoint host. The endpoint becomes invalid until its host is
     * successfully set.
     * <p>
     * <b>Parameters</b>
     * <ul>
     *   <li>{@code endpoint} should point to initialized endpoint</li>
     *   <li>{@code host} specifies FQDN, IPv4 address, or IPv6 address</li>
     * </ul>
     * <p>
     * <b>Returns</b>
     * <ul>
     *   <li>returns zero if host was successfully set</li>
     *   <li>returns a negative value on invalid arguments</li>
     *   <li>returns a negative value on allocation failure</li>
     * </ul>
     * <p>
     * <b>Ownership</b>
     * <ul>
     *   <li>doesn't take or share the ownership of {@code host} ; it may be safely deallocated after
     *       the function returns</li>
     * </ul>
     */
    private String host;

    /**
     * Set endpoint port.
     * <p>
     * When binding an endpoint, the port may be set to zero to select a random port. The
     * selected port will be then written back to the endpoint. When connecting an endpoint,
     * the port should be positive.
     * <p>
     * If port is not set, the standard port for endpoint protocol is used. This is allowed
     * only if the protocol defines its standard port.
     * <p>
     * If port is already set, it can be unset by setting to special value "-1".
     * <p>
     * On failure, invalidates endpoint port. The endpoint becomes invalid until its port is
     * successfully set.
     * <p>
     * <b>Parameters</b>
     * <ul>
     *   <li>{@code endpoint} should point to initialized endpoint</li>
     *   <li>{@code port} specifies UDP or TCP port in range [0; 65535]</li>
     * </ul>
     * <p>
     * <b>Returns</b>
     * <ul>
     *   <li>returns zero if port was successfully set</li>
     *   <li>returns a negative value on invalid arguments</li>
     * </ul>
     */
    private int port;

    /**
     * Set endpoint resource (path and query).
     * <p>
     * Path and query are both optional. Any of them may be omitted. If path is present, it
     * should be absolute.
     * <p>
     * The given resource should be percent-encoded by user if it contains special
     * characters. It may be inserted into the URI as is.
     * <p>
     * If resource is already set, it can be unset by setting to NULL or "".
     * <p>
     * On failure, invalidates endpoint resource. The endpoint becomes invalid until its
     * resource is successfully set.
     * <p>
     * <b>Parameters</b>
     * <ul>
     *   <li>{@code endpoint} should point to initialized endpoint</li>
     *   <li>{@code encoded_resource} specifies percent-encoded path and query</li>
     * </ul>
     * <p>
     * <b>Returns</b>
     * <ul>
     *   <li>returns zero if resource was successfully set</li>
     *   <li>returns a negative value on invalid arguments</li>
     *   <li>returns a negative value on allocation failure</li>
     * </ul>
     * <p>
     * <b>Ownership</b>
     * <ul>
     *   <li>doesn't take or share the ownership of {@code encoded_resource} ; it may be safely
     *       deallocated after the function returns</li>
     * </ul>
     */
    private String resource;

    /**
     * Create endpoint from URI
     *
     * @param uri   URI to parse
     *
     * @throws IllegalArgumentException  if URI is invalid
     */
    public Endpoint(String uri) {
        nativeParseUri(uri);
    }

    /**
     * Create endpoint from components
     *
     * @param protocol protocol
     * @param host     host specifies FQDN, IPv4 address, or IPv6 address
     * @param port     port specifies UDP or TCP port in range [0; 65535], or -1 to
     *                 leave the port unset
     *                 <p>
     *                 When binding an endpoint, the port may be set to zero to select a random port.
     *                 The selected port will then be written back to the endpoint. When connecting
     *                 an endpoint, the port should be positive.
     *                 <p>
     *                 If the port is unset, the standard port for the endpoint protocol is used.
     *                 This is allowed only if the protocol defines a standard port.
     * @param resource resource is nullable. Specifies percent-encoded path and query
     *
     * @throws IllegalArgumentException  if URI components don't form a valid URI
     */
    public Endpoint(Protocol protocol, String host, int port, String resource) {
        this.protocol = Check.notNull(protocol, "endpoint protocol");
        this.host = Check.notEmpty(host, "endpoint host");
        this.port = port;
        this.resource = resource;
        nativeValidate();
    }

    /**
     * Create endpoint from components
     *
     * @param protocol protocol
     * @param host     host specifies FQDN, IPv4 address, or IPv6 address
     * @param port     port specifies UDP or TCP port in range [0; 65535], or -1 to
     *                 leave the port unset
     *                 <p>
     *                 When binding an endpoint, the port may be set to zero to select a random port.
     *                 The selected port will then be written back to the endpoint. When connecting
     *                 an endpoint, the port should be positive.
     *                 <p>
     *                 If the port is unset, the standard port for the endpoint protocol is used.
     *                 This is allowed only if the protocol defines a standard port.
     *
     * @throws IllegalArgumentException  if URI components don't form a valid URI
     */
    public Endpoint(Protocol protocol, String host, int port) {
        this(protocol, host, port, null);
    }

    /**
     * Get string URI describing this endpoint.
     */
    public String getUri() {
        return nativeFormatUri();
    }

    /**
     * Get string URI describing this endpoint.
     */
    @Override
    public String toString() {
        return getUri();
    }

    private native void nativeParseUri(String uri);
    private native String nativeFormatUri();
    private native void nativeValidate();
}
