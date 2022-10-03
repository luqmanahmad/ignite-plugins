package com.ig.segmentation.internal.processors.segmentation;

import com.ig.segmentation.network.segment.ClusterQuorumResolver;
import com.ig.segmentation.plugin.IgSegmentationPlugin;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.processors.GridProcessorAdapter;
import org.apache.ignite.internal.processors.segmentation.GridSegmentationProcessor;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.LT;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.plugin.segmentation.SegmentationResolver;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class defines {@link GridSegmentationProcessor} implementation which can be replaced with
 * {@link org.apache.ignite.internal.processors.segmentation.os.GridOsSegmentationProcessor}
 * through {@link IgSegmentationPlugin}
 */
public class IgSegmentationProcessor extends GridProcessorAdapter implements GridSegmentationProcessor {

    // Segmentation start and ending messages
    private static final String SEG_START_MSG = "Starting segment checks now.";
    private static final String SEG_END_MSG = "Segmentation checks finished successfully.";

    // Segmentation resolver start and ending messages
    private static final String SEG_RESOLVER_START_MSG = "Starting segment resolver [resolver= %s, attempt=%d]";
    private static final String SEG_RESOLVER_END_MSG = "Segment resolver processed [resolver=%s, attempt=%d, valid segment=%b]";

    // Segmentation resolver error messages
    private static final String SEG_RESOLVER_ERROR_MSG = "Segmentation resolver failed [resolver=%s, attempt=%d, error message=%s]";

    // No resolvers message
    private static final String NO_RESOLVERS_MSG = "No segment resolvers found in ignite configuration. " +
            "For more information see org.apache.ignite.configuration.IgniteConfiguration.setSegmentationResolvers";

    /**
     * @param ctx Kernal context.
     */
    public IgSegmentationProcessor(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    public boolean isValidSegment() {
        List<SegmentationResolver> resolvers = getSegmentationResolvers();

        if (F.isEmpty(resolvers)) {
            if (isLogDebugModeEnabled())
                this.log.debug(NO_RESOLVERS_MSG);

            return true;
        }

        // stores all the errors
        Map<String, IgniteBiTuple> errors = new TreeMap<String, IgniteBiTuple>();

        if (!hasValidSegments(resolvers, errors)) {
            for (String key : errors.keySet()) {
                IgniteBiTuple<SegmentationResolver, IgniteCheckedException> tuple = errors.get(key);
                LT.error(this.log, tuple.get2(), key + tuple.get1());
            }

            return false;
        }

        return true;
    }

    /**
     * @param resolvers List of all the segment resolvers
     * @param errors    Map for keeping all the errors against each resolver
     * @return true if the segment check pass else false
     */
    private boolean hasValidSegments(List<SegmentationResolver> resolvers, Map<String, IgniteBiTuple> errors) {
        if (isLogDebugModeEnabled())
            this.log.debug(SEG_START_MSG);

        boolean validSegment = false;

        for (SegmentationResolver resolver : resolvers) {
            String resolverName = U.getSimpleName(resolver.getClass());

            for (int i = 1; i <= getIgniteConfiguration().getSegmentationResolveAttempts(); i++) {
                try {
                    if (isLogDebugModeEnabled())
                        this.log.debug( String.format(SEG_RESOLVER_START_MSG, resolverName, i) );

                    if (resolver instanceof ClusterQuorumResolver)
                        validSegment = ((ClusterQuorumResolver) resolver).isQuorum(this.ctx);
                    else validSegment = resolver.isValidSegment();

                    if (isLogDebugModeEnabled())
                        this.log.debug( String.format(SEG_RESOLVER_END_MSG, resolverName, i, validSegment) );
                }
                catch (IgniteCheckedException exception) {
                    String errorMessage = String.format(SEG_RESOLVER_ERROR_MSG, resolverName, i, exception.getMessage());
                    errors.put(errorMessage, F.t(resolver, exception));

                    if (isLogDebugModeEnabled())
                        this.log.debug(errorMessage);
                }
            }

            // no need to check other segments
            if (!passAllSegmentationResolvers() && validSegment)
                break;

            // invalid segment
            if (passAllSegmentationResolvers() && !validSegment)
                return false;
        }

        if (isLogDebugModeEnabled())
            this.log.debug(SEG_END_MSG);

        return validSegment;
    }

    private boolean isLogDebugModeEnabled() {
        return this.log.isDebugEnabled();
    }

    /**
     * @return true if all segment needs to be resolved else false
     */
    private boolean passAllSegmentationResolvers() {
        return getIgniteConfiguration().isAllSegmentationResolversPassRequired();
    }

    /**
     * @return Segmentation resolvers from {@link IgniteConfiguration#getSegmentationResolvers()}
     */
    private List<SegmentationResolver> getSegmentationResolvers() {
        return F.asList(getIgniteConfiguration().getSegmentationResolvers());
    }

    /**
     * @return {@link IgniteConfiguration} from {@link GridKernalContext#config()}
     */
    private IgniteConfiguration getIgniteConfiguration() {
        return this.ctx.config();
    }
}
