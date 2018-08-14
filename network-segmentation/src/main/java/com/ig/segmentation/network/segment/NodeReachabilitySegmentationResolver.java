package com.ig.segmentation.network.segment;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.internal.util.typedef.internal.S;

import com.ig.segmentation.network.exception.IgSegmentationException;
import com.ig.segmentation.plugin.segmentation.IgSegmentationResolver;


/**
 * Node Reachability Resolver will check whether an address is reachable through the host machine
 * and validate the segment accordingly.
 */
public class NodeReachabilitySegmentationResolver implements IgSegmentationResolver {

    /** Default timeout */
    private int DFLT_TIMEOUT  = 1000;

    /** Default time to live */
    private int DFLT_TTL = 0;

    private int timeout = DFLT_TIMEOUT;
    private int ttl = DFLT_TTL;

    private boolean initialized = false;

    private InetAddress targetNodeAddress;
    private InetAddress localNodeAddress;
    private NetworkInterface localNodeNetworkInterface;

    /**
     * @return true if the segment is valid else false
     * @throws IgSegmentationException if an error occurred
     */
    public boolean isValidSegment() throws IgSegmentationException {
        if (!initialized)
            this.validateProperties();

        try {
            return this.targetNodeAddress.isReachable(this.localNodeNetworkInterface, this.ttl, this.timeout);
        }
        catch (IOException e) {
            throw new IgSegmentationException("Failed to check node reachability: " + this.targetNodeAddress, e);
        }
    }

    /**
     * Set timeout to use when checking reachibility
     *
     * @param timeout the time, in milliseconds
     * @return {@code this} for chaining.
     */
    public NodeReachabilitySegmentationResolver setTimeout(int timeout) {
        this.timeout = timeout;

        return this;
    }

    /**
     * Sets time to live (TTL) to use when checking reachability.
     *
     * @param ttl the maximum numbers of hops to try
     * @return {@code this} for chaining.
     */
    public NodeReachabilitySegmentationResolver setTtl(int ttl) {
        this.ttl = ttl;

        return this;
    }

    /**
     * Sets the local node address
     *
     * <p>
     * This is a required field in case {@link NodeReachabilitySegmentationResolver#setLocalNodeName} is not used.
     * </p>
     *
     * @param localNodeAddress Local node address
     * @return {@code this} for chaining.
     */
    public NodeReachabilitySegmentationResolver setLocalNodeAddress(InetAddress localNodeAddress) {
        this.localNodeAddress = localNodeAddress;

        return this;
    }

    /**
     * Sets the local node host name
     *
     * <p>
     * This is a required field in case {@link NodeReachabilitySegmentationResolver#setLocalNodeAddress} is not used.
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
     * @throws IgniteCheckedException
     */
    public NodeReachabilitySegmentationResolver setLocalNodeName(String localNodeName) throws IgniteCheckedException {
        A.notNull(localNodeName, "localNodeName");

        this.localNodeAddress = getInetAddressByName(localNodeName);

        return this;
    }

    /**
     * Sets network interface to be used, in-case local host node IP is associated with multiple network interfaces.
     *
     * <p>
     * This is optional, but it is always a good practice to provide the network interface
     * explicitly else it will pick up the first available interface associated with the IP address.
     * </p>
     *
     * @param localNodeNetworkInterface Network interface address for the local node
     * @return {@code this} for chaining.
     */
    public NodeReachabilitySegmentationResolver setLocalNodeNetworkInterfaceToUse(NetworkInterface localNodeNetworkInterface) {
        this.localNodeNetworkInterface = localNodeNetworkInterface;

        return this;
    }

    /**
     * Sets the target node address which needs to be checked by the resolver
     *
     * <p>
     * This is a required field in case {@link NodeReachabilitySegmentationResolver#setTargetNodeName} is not used.
     * </p>
     *
     * @param targetNodeAddress Target node address which needs to be checked by the resolver
     * @return {@code this} for chaining.
     */
    public NodeReachabilitySegmentationResolver setTargetNodeAddress(InetAddress targetNodeAddress) {
        A.notNull(targetNodeAddress, "targetNodeAddress");

        this.targetNodeAddress = targetNodeAddress;

        return this;
    }

    /**
     * Sets the target node host name which needs to be checked by the resolver
     *
     * <p>
     * This is a required field in case {@link NodeReachabilitySegmentationResolver#setTargetNodeAddress} is not used.
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
    public NodeReachabilitySegmentationResolver setTargetNodeName(String targetNodeName) throws IgSegmentationException {
        A.notNull(targetNodeName, "targetNodeName");

        this.targetNodeAddress = getInetAddressByName(targetNodeName);

        return this;
    }

    public String toString() {
        return S.toString(NodeReachabilitySegmentationResolver.class, this);
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
        catch (UnknownHostException e) {
            throw new IgSegmentationException("Failed to get address by name for host: " + hostName, e);
        }
    }

    /**
     * Validates all the user defined parameters.
     *
     * @throws IgSegmentationException in case any errors occurred
     */
    private void validateProperties() throws IgSegmentationException {

        if (this.ttl < 0)
            throw new IgSegmentationException("Time to live should be greater than 0");

        if (this.timeout < 0)
            throw new IgSegmentationException("Timeout should be greater than 0");

        if (this.localNodeAddress == null)
            A.notNull(localNodeAddress, "[Use setLocalNodeAddress(..) method to set the property]");

        if (this.targetNodeAddress == null)
            A.notNull(targetNodeAddress, "[Use setTargetNodeAddress(..) method to set the property]");

        try {
            if (this.localNodeNetworkInterface == null)
                this.localNodeNetworkInterface = NetworkInterface.getByInetAddress(this.localNodeAddress);
        }
        catch (SocketException e) {
            throw new IgSegmentationException("Failed to get network interface for " + localNodeAddress.toString(), e);
        }

        initialized = true;
    }
}
