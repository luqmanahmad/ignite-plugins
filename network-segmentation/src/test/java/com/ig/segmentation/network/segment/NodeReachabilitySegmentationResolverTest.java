package com.ig.segmentation.network.segment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ig.segmentation.network.exception.IgSegmentationException;

public class NodeReachabilitySegmentationResolverTest {

    private NodeReachabilitySegmentationResolver resolver;

    @Before
    public void initialise() {
        resolver = new NodeReachabilitySegmentationResolver();
    }

    @Test(expected = IgSegmentationException.class)
    public void test_resolver_with_negative_ttl() throws IgSegmentationException {
        resolver.setTtl(-1);
        resolver.isValidSegment();
    }

    @Test(expected = IgSegmentationException.class)
    public void test_resolver_with_negative_timeout() throws IgSegmentationException {
        resolver.setTimeout(-1);
        resolver.isValidSegment();
    }

    @Test(expected = NullPointerException.class)
    public void test_resolver_without_local_node_address() throws IgSegmentationException {
        resolver.isValidSegment();
    }

    @Test(expected = NullPointerException.class)
    public void test_resolver_without_target_node_address() throws IgSegmentationException {
        resolver.setLocalNodeName("localhost");
        resolver.isValidSegment();
    }

    @Test
    public void test_resolver_with_defaults() throws IgSegmentationException {
        resolver.setLocalNodeName("localhost");
        resolver.setTargetNodeName("localhost");
        Assert.assertTrue(resolver.isValidSegment());
    }
}
