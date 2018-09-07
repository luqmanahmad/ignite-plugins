package com.ig.segmentation.network.segment;

import java.net.InetAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ig.segmentation.network.exception.IgSegmentationException;

public class TcpIpSegmentationResolverTest {

    private TcpIpSegmentationResolver resolver;

    @Before
    public void initialise() {
        resolver = new TcpIpSegmentationResolver();
    }

    @Test(expected = NullPointerException.class)
    public void test_resolver_without_local_node_address() throws IgSegmentationException {
        resolver.isValidSegment();
    }

    @Test(expected = IgSegmentationException.class)
    public void test_resolver_with_local_node_negative_port() throws IgSegmentationException {
        resolver.setLocalNodeName("localhost");
        resolver.setLocalNodePort(-1);
        resolver.isValidSegment();
    }

    @Test(expected = NullPointerException.class)
    public void test_resolver_without_target_node_address() throws IgSegmentationException {
        resolver.setLocalNodeName("localhost");
        resolver.isValidSegment();
    }

    @Test(expected = IgSegmentationException.class)
    public void test_resolver_with_target_node_negative_port() throws IgSegmentationException {
        resolver.setLocalNodeName("localhost");
        resolver.setTargetNodeName("localhost");
        resolver.setLocalNodePort(-1);
        resolver.setTargetNodePort(-1);
        resolver.isValidSegment();
    }

    @Test(expected = IgSegmentationException.class)
    public void test_resolver_with_negative_socket_timeout() throws IgSegmentationException {
        resolver.setLocalNodeName("localhost");
        resolver.setTargetNodeName("localhost");
        resolver.setTargetNodePort(80);
        resolver.setSocketTimeout(-1);
        resolver.isValidSegment();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_resolver_with_invalid_local_port() throws Exception {
        resolver.setLocalNodeName("localhost");
        resolver.setLocalNodePort(76262);
        resolver.setTargetNodeName("localhost");
        resolver.isValidSegment();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_resolver_with_invalid_target_port() throws Exception {
        resolver.setLocalNodeName("localhost");
        resolver.setTargetNodeName("localhost");
        resolver.setTargetNodePort(76262);
        resolver.isValidSegment();
    }

    @Test
    public void test_resolver_with_defaults() throws Exception {
        resolver.setLocalNodeAddress(InetAddress.getLocalHost());
        resolver.setTargetNodeName("www.google.com");
        resolver.setTargetNodePort(80);
        Assert.assertTrue(resolver.isValidSegment());
    }
}
