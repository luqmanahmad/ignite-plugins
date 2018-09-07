package com.ig.security.internal.processors.security;

import java.util.Collection;
import java.util.UUID;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.processors.GridProcessorAdapter;
import org.apache.ignite.internal.processors.security.GridSecurityProcessor;
import org.apache.ignite.internal.processors.security.SecurityContext;
import org.apache.ignite.plugin.security.AuthenticationContext;
import org.apache.ignite.plugin.security.SecurityCredentials;
import org.apache.ignite.plugin.security.SecurityException;
import org.apache.ignite.plugin.security.SecurityPermission;
import org.apache.ignite.plugin.security.SecuritySubject;
import org.jetbrains.annotations.Nullable;

/**
 * This class defines {@link GridSecurityProcessor} implementation which can be replaced with
 * {@link org.apache.ignite.internal.processors.security.os.GridOsSecurityProcessor}
 * through {@link com.ig.security.plugin.IgSecurityPlugin}
 */
public class IgSecurityProcessor extends GridProcessorAdapter implements GridSecurityProcessor {

    /**
     * @param ctx Kernal context.
     */
    public IgSecurityProcessor(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    public SecurityContext authenticateNode(ClusterNode node, SecurityCredentials cred) throws IgniteCheckedException {
        return null;
    }

    /** {@inheritDoc} */
    public boolean isGlobalNodeAuthentication() {
        return false;
    }

    /** {@inheritDoc} */
    public SecurityContext authenticate(AuthenticationContext ctx) throws IgniteCheckedException {
        return null;
    }

    /** {@inheritDoc} */
    public Collection<SecuritySubject> authenticatedSubjects() throws IgniteCheckedException {
        return null;
    }

    /** {@inheritDoc} */
    public SecuritySubject authenticatedSubject(UUID subjId) throws IgniteCheckedException {
        return null;
    }

    /** {@inheritDoc} */
    public void authorize(String name, SecurityPermission perm, @Nullable SecurityContext securityCtx) throws SecurityException {

    }

    /** {@inheritDoc} */
    public void onSessionExpired(UUID subjId) {

    }

    /** {@inheritDoc} */
    public boolean enabled() {
        return false;
    }
}
