package com.ig.security.plugin;

import com.ig.security.plugin.configuration.SecurityPluginConfiguration;
import com.ig.security.plugin.provider.SecurityPluginProvider;

/**
 * Security plugin implementation
 */
public class IgSecurityPluginImpl implements IgSecurityPlugin
{

    private SecurityPluginProvider securityPluginProvider;

    public IgSecurityPluginImpl(SecurityPluginProvider segmentationPluginProvider) {
        this.securityPluginProvider = segmentationPluginProvider;
    }

    public SecurityPluginConfiguration getPluginConfiguration() {
        return securityPluginProvider.getPluginConfiguration();
    }
}
