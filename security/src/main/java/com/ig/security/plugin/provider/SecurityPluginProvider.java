package com.ig.security.plugin.provider;

import java.io.Serializable;
import java.util.UUID;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.processors.security.GridSecurityProcessor;
import org.apache.ignite.plugin.CachePluginContext;
import org.apache.ignite.plugin.CachePluginProvider;
import org.apache.ignite.plugin.ExtensionRegistry;
import org.apache.ignite.plugin.IgnitePlugin;
import org.apache.ignite.plugin.PluginConfiguration;
import org.apache.ignite.plugin.PluginContext;
import org.apache.ignite.plugin.PluginProvider;
import org.apache.ignite.plugin.PluginValidationException;
import org.jetbrains.annotations.Nullable;

import com.ig.security.internal.processors.security.IgSecurityProcessor;
import com.ig.security.plugin.configuration.SecurityPluginConfiguration;
import com.ig.security.plugin.IgSecurityPlugin;
import com.ig.security.plugin.IgSecurityPluginImpl;

/**
 * Security plugin provider implementations
 */
public class SecurityPluginProvider implements PluginProvider<SecurityPluginConfiguration> {

    private static final String PLUGIN_NAME = "IG_SECURITY_PLUGIN";
    private static final String COPYRIGHT = "2018 Copyright(C) Innovate Grid";
    private static final String VERSION = "0.0.1";

    private SecurityPluginConfiguration pluginConfiguration;
    private IgSecurityPlugin plugin;

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

    public void initExtensions(PluginContext ctx, ExtensionRegistry registry) {
        this.initPluginConfiguration(ctx);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
    public <T> T createComponent(PluginContext ctx, Class<T> cls) {

        if (cls.equals(GridSecurityProcessor.class))
        {
            if (ctx.grid().log().isDebugEnabled()) {
                ctx.grid().log().debug("Creating " + PLUGIN_NAME);
            }

            return (T) new IgSecurityProcessor(((IgniteKernal) ctx.grid()).context());
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

    public SecurityPluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }

    private void initPluginConfiguration(PluginContext ctx) {
        IgniteConfiguration configuration = ctx.igniteConfiguration();

        if (configuration.getPluginConfigurations() != null) {
            PluginConfiguration[] pluginConfigurations = configuration.getPluginConfigurations();

            for(int i = 0; i < pluginConfigurations.length; i++) {
                PluginConfiguration pluginConfiguration = pluginConfigurations[i];

                if (pluginConfiguration instanceof SecurityPluginConfiguration)
                    this.pluginConfiguration = (SecurityPluginConfiguration) pluginConfiguration;
            }
        }

        // in-case on the class path but didn't initialise it using the ignite configuration
        if (pluginConfiguration == null)
            pluginConfiguration = new SecurityPluginConfiguration();

        plugin = new IgSecurityPluginImpl(this);
    }
}
