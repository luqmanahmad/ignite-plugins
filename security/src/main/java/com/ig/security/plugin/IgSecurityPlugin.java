package com.ig.security.plugin;

import org.apache.ignite.plugin.IgnitePlugin;

import com.ig.security.plugin.configuration.SecurityPluginConfiguration;

/**
 * Plugin for security
 */
public interface IgSecurityPlugin extends IgnitePlugin {

    /**
     * @return Security plugin configuration
     */
    SecurityPluginConfiguration getPluginConfiguration();

}
