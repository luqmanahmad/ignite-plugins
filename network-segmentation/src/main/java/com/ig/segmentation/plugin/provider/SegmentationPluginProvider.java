package com.ig.segmentation.plugin.provider;

import java.io.Serializable;
import java.util.UUID;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.processors.segmentation.GridSegmentationProcessor;
import org.apache.ignite.plugin.CachePluginContext;
import org.apache.ignite.plugin.CachePluginProvider;
import org.apache.ignite.plugin.ExtensionRegistry;
import org.apache.ignite.plugin.IgnitePlugin;
import org.apache.ignite.plugin.PluginConfiguration;
import org.apache.ignite.plugin.PluginContext;
import org.apache.ignite.plugin.PluginProvider;
import org.apache.ignite.plugin.PluginValidationException;
import org.jetbrains.annotations.Nullable;

import com.ig.segmentation.internal.processors.segmentation.IgSegmentationProcessor;
import com.ig.segmentation.plugin.IgSegmentationPluginImpl;
import com.ig.segmentation.plugin.configuration.SegmentationPluginConfiguration;
import com.ig.segmentation.plugin.IgSegmentationPlugin;

public class SegmentationPluginProvider implements PluginProvider<SegmentationPluginConfiguration> {

    private static final String PLUGIN_NAME = "IG_NS_PLUGIN";
    private static final String COPYRIGHT = "2018 Copyright(C) Innovate Grid";
    private static final String VERSION = "0.0.1";

    private SegmentationPluginConfiguration pluginConfiguration;
    private IgSegmentationPlugin plugin;

    public String name() {
        return PLUGIN_NAME;
    }

    public String version() {
        return VERSION;
    }

    public String copyright() {
        return COPYRIGHT;
    }

    @SuppressWarnings("unchecked")
    public <T extends IgnitePlugin> T plugin() {
        return (T) plugin;
    }

    public void initExtensions(PluginContext ctx, ExtensionRegistry registry) {
        this.initPluginConfiguration(ctx);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T createComponent(PluginContext ctx, Class<T> cls) {

        if (cls.equals(GridSegmentationProcessor.class))
        {
            if (ctx.grid().log().isDebugEnabled()) {
                ctx.grid().log().debug("Creating " + PLUGIN_NAME);
            }

            return (T) new IgSegmentationProcessor(((IgniteKernal) ctx.grid()).context());
        }
        return null;
    }

    public CachePluginProvider createCacheProvider(CachePluginContext ctx) {
        return null;
    }

    public void start(PluginContext ctx) throws IgniteCheckedException {

    }

    public void stop(boolean cancel) throws IgniteCheckedException {

    }

    public void onIgniteStart() throws IgniteCheckedException {

    }

    public void onIgniteStop(boolean cancel) {

    }

    @Nullable
    public Serializable provideDiscoveryData(UUID nodeId) {
        return null;
    }

    public void receiveDiscoveryData(UUID nodeId, Serializable data) {

    }

    public void validateNewNode(ClusterNode node) throws PluginValidationException {

    }

    public SegmentationPluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }

    private void initPluginConfiguration(PluginContext ctx) {
        IgniteConfiguration configuration = ctx.igniteConfiguration();

        if (configuration.getPluginConfigurations() != null) {
            PluginConfiguration[] pluginConfigurations = configuration.getPluginConfigurations();

            for(int i = 0; i < pluginConfigurations.length; i++) {
                PluginConfiguration pluginConfiguration = pluginConfigurations[i];

                if (pluginConfiguration instanceof SegmentationPluginConfiguration)
                    this.pluginConfiguration = (SegmentationPluginConfiguration) pluginConfiguration;
            }
        }

        // in-case on the class path but didn't initialise it using the ignite configuration
        if (pluginConfiguration == null)
            pluginConfiguration = new SegmentationPluginConfiguration();

        plugin = new IgSegmentationPluginImpl(this);
    }
}
