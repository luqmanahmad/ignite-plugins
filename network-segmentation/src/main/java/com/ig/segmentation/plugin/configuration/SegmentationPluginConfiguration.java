package com.ig.segmentation.plugin.configuration;

import org.apache.ignite.plugin.PluginConfiguration;

/**
 * Network segmentation plugin configuration
 */
public class SegmentationPluginConfiguration implements PluginConfiguration {

    private SegmentationPluginConfiguration configuration;

    public SegmentationPluginConfiguration() {
    }

    public SegmentationPluginConfiguration(SegmentationPluginConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setConfiguration(SegmentationPluginConfiguration configuration) {
        this.configuration = configuration;
    }

    public SegmentationPluginConfiguration getConfiguration() {
        return configuration;
    }
}
