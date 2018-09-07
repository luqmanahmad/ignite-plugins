package com.ig.security.plugin.configuration;

import org.apache.ignite.plugin.PluginConfiguration;

/**
 * Security plugin configuration
 */
public class SecurityPluginConfiguration implements PluginConfiguration {

    private SecurityPluginConfiguration configuration;

    public SecurityPluginConfiguration() {
    }

    public SecurityPluginConfiguration(SecurityPluginConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setConfiguration(SecurityPluginConfiguration configuration) {
        this.configuration = configuration;
    }

    public SecurityPluginConfiguration getConfiguration() {
        return configuration;
    }
}
