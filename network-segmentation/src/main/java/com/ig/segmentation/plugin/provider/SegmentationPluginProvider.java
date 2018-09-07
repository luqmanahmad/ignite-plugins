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

/**
 * Network segmentation plugin provider implementations
 */
public class SegmentationPluginProvider implements PluginProvider<SegmentationPluginConfiguration> {

    private static final String PLUGIN_NAME = "IG_NS_PLUGIN";
    private static final String COPYRIGHT = "2018 Copyright(C) Innovate Grid";
    private static final String VERSION = "0.0.1";

    private SegmentationPluginConfiguration pluginConfiguration;
    private IgSegmentationPlugin plugin;

    /** {@inheritDoc} */
    public String name() {
        return PLUGIN_NAME;
    }

    /** {@inheritDoc} */
    public String version() {
        return VERSION;
    }

    /** {@inheritDoc} */
    public String copyright() {
        return COPYRIGHT;
    }

    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
    public <T extends IgnitePlugin> T plugin() {
        return (T) plugin;
    }

    /** {@inheritDoc} */
    public void initExtensions(PluginContext ctx, ExtensionRegistry registry) {
        this.initPluginConfiguration(ctx);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public CachePluginProvider createCacheProvider(CachePluginContext ctx) {
        return null;
    }

    /** {@inheritDoc} */
    public void start(PluginContext ctx) throws IgniteCheckedException {

    }

    /** {@inheritDoc} */
    public void stop(boolean cancel) throws IgniteCheckedException {

    }

    /** {@inheritDoc} */
    public void onIgniteStart() throws IgniteCheckedException {

    }

    /** {@inheritDoc} */
    public void onIgniteStop(boolean cancel) {

    }

    @Nullable
    /** {@inheritDoc} */
    public Serializable provideDiscoveryData(UUID nodeId) {
        return null;
    }

    /** {@inheritDoc} */
    public void receiveDiscoveryData(UUID nodeId, Serializable data) {

    }

    /** {@inheritDoc} */
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
