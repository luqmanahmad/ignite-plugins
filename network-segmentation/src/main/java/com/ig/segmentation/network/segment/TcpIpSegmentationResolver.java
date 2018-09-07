package com.ig.segmentation.network.segment;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;

import com.ig.segmentation.network.exception.IgSegmentationException;
import com.ig.segmentation.plugin.segmentation.IgSegmentationResolver;

/**
 * TcpIpSegmentationResolver which can be used to check connection between different apps
 * and closing it immediately. In case the connection is not successful the segment will fail.
 *
 * <p>
 * See example 1 on this url for more information
 * </p>
 *
 * https://www.programcreek.com/java-api-examples/?class=java.net.Socket&method=connect
 */
public class TcpIpSegmentationResolver implements IgSegmentationResolver {

    /** Default socket time out */
    private int DFLT_SOCKET_TIMEOUT  = 2000;

    /** Default local node port */
    private int DFLT_LOCAL_PORT = 0;

    private InetAddress localNodeAddress;
    private InetAddress targetNodeAddress;

    private int localNodePort = DFLT_LOCAL_PORT;
    private int targetNodePort;

    private int socketTimeout = DFLT_SOCKET_TIMEOUT;

    private Socket endpointCommunicator;

    private InetSocketAddress bindPointAddress;
    private InetSocketAddress endPointAddress;

    private boolean initialized = false;

    /** {@inheritDoc} */
    public boolean isValidSegment() throws IgSegmentationException
    {
        if (!initialized)
            this.validateProperties();

        try {
            endpointCommunicator = new Socket();
            endpointCommunicator.bind(bindPointAddress);
            endpointCommunicator.connect(endPointAddress, this.socketTimeout);
        }
        catch (IOException e) {
            throw new IgSegmentationException("Failed to check tcp reachability: " + this.targetNodeAddress.toString(), e);
        }
        finally {
            U.closeQuiet(endpointCommunicator);
        }

        return true;
    }

    /**
     * Socket timeout value after which the connection will give up. Default is 2000ms
     *
     * @param socketTimeout the timeout value to be used in milliseconds for sockets.
     * @return
     */
    public TcpIpSegmentationResolver setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;

        return this;
    }

    /**
     * Sets the target node address which needs to be checked by the resolver
     *
     * <p>
     * This is a required field in case {@link TcpIpSegmentationResolver#setTargetNodeName} is not used.
     * </p>
     *
     * @param targetNodeAddress Target node address which needs to be checked by the resolver
     * @return {@code this} for chaining.
     */
    public TcpIpSegmentationResolver setTargetNodeAddress(InetAddress targetNodeAddress) {
        A.notNull(targetNodeAddress, "targetNodeAddress");

        this.targetNodeAddress = targetNodeAddress;

        return this;
    }

    /**
     * Sets the target node host name which needs to be checked by the resolver
     *
     * <p>
     * This is a required field in case {@link TcpIpSegmentationResolver#setTargetNodeAddress} is not used.
     * </p>
     *
     * <p>
     * The host name can either be a machine name, such as "{@code java.sun.com}", or a textual
     * representation of its IP address. If a literal IP address is supplied, only the validity
     * of the address format is checked.
     * </p>
     *
     * @param targetNodeName Target node name
     * @throws IgSegmentationException if no IP address for the {@code targetNodeName} could be found
     * @return {@code this} for chaining.
     */
    public TcpIpSegmentationResolver setTargetNodeName(String targetNodeName) throws IgSegmentationException {
        A.notNull(targetNodeName, "targetNodeName");

        this.targetNodeAddress = getInetAddressByName(targetNodeName);

        return this;
    }

    /**
     * Sets the target node port
     *
     * @param targetNodePort Target node port on which it needs to connect - Valid values between 0 and 65535
     * @return {@code this} for chaining.
     */
    public TcpIpSegmentationResolver setTargetNodePort(int targetNodePort) {
        this.targetNodePort = targetNodePort;

        return this;
    }

    /**
     * Sets the local node address
     *
     * <p>
     * This is a required field in case {@link TcpIpSegmentationResolver#setLocalNodeName} is not used.
     * </p>
     *
     * @param localNodeAddress Local node address
     * @return {@code this} for chaining.
     */
    public TcpIpSegmentationResolver setLocalNodeAddress(InetAddress localNodeAddress) {
        A.notNull(localNodeAddress, "localNodeAddress");

        this.localNodeAddress = localNodeAddress;

        return this;
    }

    /**
     * Sets the local node host name
     *
     * <p>
     * This is a required field in case {@link TcpIpSegmentationResolver#setLocalNodeAddress} is not used.
     * </p>
     *
     * <p>
     * The host name can either be a machine name, such as "{@code java.sun.com}", or a textual
     * representation of its IP address. If a literal IP address is supplied, only the validity
     * of the address format is checked.
     * </p>
     *
     * @param localNodeName Local host node name
     * @return {@code this} for chaining.
     * @throws IgSegmentationException
     */
    public TcpIpSegmentationResolver setLocalNodeName(String localNodeName) throws IgSegmentationException {
        A.notNull(localNodeName, "localNodeName");

        this.localNodeAddress = getInetAddressByName(localNodeName);

        return this;
    }

    /**
     * Sets the local node port - Default is 0
     *
     * @param localNodePort Local node port - Valid values between 0 and 65535
     * @return {@code this} for chaining.
     */
    public TcpIpSegmentationResolver setLocalNodePort(int localNodePort) {
        this.localNodePort = localNodePort;

        return this;
    }

    public String toString() {
        return S.toString(TcpIpSegmentationResolver.class, this);
    }

    /**
     * Validates all the user defined parameters.
     *
     * @throws IgSegmentationException in case any errors occurred
     */
    private void validateProperties() throws IgSegmentationException {

        if (localNodeAddress == null) {
            A.notNull(localNodeAddress, "[Use setLocalNodeAddress(..) method to set the property]");
        }

        if (this.localNodePort < 0) {
            throw new IgSegmentationException("Invalid local node port. Valid values are between 0 and 65535." + this);
        }

        if (targetNodeAddress == null) {
            A.notNull(targetNodeAddress, "[Use setTargetNodeAddress(..) method to set the property]");
        }

        if (this.targetNodePort < 0) {
            throw new IgSegmentationException("Invalid target node port. Valid values are between 0 and 65535." + this);
        }

        if (this.socketTimeout < 0) {
            throw new IgSegmentationException("Invalid socket timeout value." + this);
        }

        this.bindPointAddress = new InetSocketAddress(this.localNodeAddress, this.localNodePort);
        this.endPointAddress = new InetSocketAddress(this.targetNodeAddress, this.targetNodePort);

        initialized = true;
    }

    /**
     * @param hostName Name of the machine
     * @return {@link InetAddress} from network address
     * @throws IgSegmentationException
     */
    private InetAddress getInetAddressByName(String hostName) throws IgSegmentationException {
        try {
            return InetAddress.getByName(hostName);
        }
        catch (UnknownHostException ex) {
            throw new IgSegmentationException("Failed to get address by name for host: " + hostName, ex);
        }
    }
}
