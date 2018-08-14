/*
 * Copyright (c) 2018.
 */

package com.ig.segmentation.plugin;

import org.apache.ignite.plugin.IgnitePlugin;

import com.ig.segmentation.plugin.configuration.SegmentationPluginConfiguration;

/**
 * Plugin for network segmentation
 */
public interface IgSegmentationPlugin extends IgnitePlugin {

    SegmentationPluginConfiguration getPluginConfiguration();

}
