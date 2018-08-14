package com.ig.segmentation.plugin;

import com.ig.segmentation.plugin.configuration.SegmentationPluginConfiguration;
import com.ig.segmentation.plugin.provider.SegmentationPluginProvider;

/**
 * Network segmentation plugin implementation
 */
public class IgSegmentationPluginImpl implements IgSegmentationPlugin {

    private SegmentationPluginProvider segmentationPluginProvider;

    public IgSegmentationPluginImpl(SegmentationPluginProvider segmentationPluginProvider) {
        this.segmentationPluginProvider = segmentationPluginProvider;
    }

    public SegmentationPluginConfiguration getPluginConfiguration() {
        return segmentationPluginProvider.getPluginConfiguration();
    }
}
