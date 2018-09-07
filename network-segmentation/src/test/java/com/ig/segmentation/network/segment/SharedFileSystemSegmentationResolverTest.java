package com.ig.segmentation.network.segment;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ig.segmentation.network.exception.IgSegmentationException;

public class SharedFileSystemSegmentationResolverTest {

    private SharedFileSystemSegmentationResolver resolver;

    @Before
    public void initialise() {
        resolver = new SharedFileSystemSegmentationResolver();
    }

    @Test(expected = NullPointerException.class)
    public void test_resolver_with_folder_not_set() throws IgSegmentationException {
        resolver.isValidSegment();
    }

    @Test(expected = IgSegmentationException.class)
    public void test_resolver_with_invalid_folder() throws IgSegmentationException {
        resolver.setFolderPath("random");
        resolver.isValidSegment();
    }

    @Test
    public void test_resolver_with_valid_folder() throws IgSegmentationException {
        File resourcesDirectory = new File("src/test/resources/SharedFolder");

        resolver.setFolderPath(resourcesDirectory.getAbsolutePath());

        Assert.assertTrue(resolver.isValidSegment());
    }

}
