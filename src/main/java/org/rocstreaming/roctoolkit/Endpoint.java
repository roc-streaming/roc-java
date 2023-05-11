package org.rocstreaming.roctoolkit;

/**
 * Network endpoint.
 * <p>
 * Endpoint is a network entry point of a peer. The definition includes the
 * protocol being used, network host and port, and, for some protocols, a
 * resource. All these parts together are unambiguously represented
 * by a URI. The user may set or get the entire URI or its individual parts.
 * </p>
 * <h3>Endpoint URI</h3>
 * <p>
 * Endpoint URI syntax is a subset of the syntax defined in RFC 3986:
 * <p>
 * protocol://host[:port][/path][?query]
 * <p>
 * Examples:
 * <ul>
 *     <li>"rtsp://localhost:123/path?query"</li>
 *     <li>"rtp+rs8m://localhost:123"</li>
 *     <li>"rtp://127.0.0.1:123"</li>
 *     <li>"rtp://[::1]:123"</li>
 * </ul>
 * <p>
 * The following protocols (schemes) are supported:
 * <ul>
 *     <li> "rtp://"       ({@link Protocol#RTP RTP})</li>
 *     <li> "rtp+rs8m://"  ({@link Protocol#RTP_RS8M_SOURCE RTP_RS8M_SOURCE})</li>
 *     <li> "rs8m://"      ({@link Protocol#RS8M_REPAIR RS8M_REPAIR})</li>
 *     <li> "rtp+ldpc://"  ({@link Protocol#RTP_LDPC_SOURCE RTP_LDPC_SOURCE})</li>
 *     <li> "ldpc://"      ({@link Protocol#LDPC_REPAIR LDPC_REPAIR})</li>
 * </ul>
 * <p>
 * The host field should be either FQDN (domain name), or IPv4 address, or
 * IPv6 address in square brackets.
 * <p>
 * The port field can be omitted if the protocol defines standard port. Otherwise,
 * the port can not be omitted. For example, RTSP defines standard port,
 * but RTP doesn't.
 * <p>
 * The path and query fields are allowed only for protocols that support them.
 * For example, they're supported by RTSP, but not by RTP.
 * <p>
 * <h3>Thread-safety</h3>
 * <p>
 * Can't be used concurrently
 */
public class Endpoint {

    static {
        RocLibrary.loadLibrary();
    }

    private Protocol protocol;

    private String host;

    private int port;

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

    /**
     * Builder class for {@link Endpoint}
     */
    public static class Builder {

        private Protocol protocol;

        private String host;

        private int port;

        private String resource;

        /**
         * Set protocol
         * @param protocol protocol
         * @return this Builder
         */
        public Builder setProtocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }


        /**
         * Set host
         * @param host host
         * @return this Builder
         */
        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        /**
         * Set port
         * @param port port
         * @return this Builder
         */
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        /**
         * Set resource
         * @param resource resource
         * @return this Builder
         */
        public Builder setResource(String resource) {
            this.resource = resource;
            return this;
        }

        public Endpoint build() {
            return new Endpoint(this.protocol, this.host, this.port, this.resource);
        }
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return getUri();
    }

    public native String getUri();

    private native void init(String uri) throws IllegalArgumentException;

    private native void validate() throws IllegalArgumentException;

}
