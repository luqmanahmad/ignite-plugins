package com.ig.segmentation.network.segment;

import com.ig.segmentation.network.exception.IgSegmentationException;
import com.ig.segmentation.plugin.segmentation.IgSegmentationResolver;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.cluster.IgniteClusterEx;

public class ClusterQuorumResolver implements IgSegmentationResolver {

    private int maxServerNodesSeen = 0;

    private int quorumAttempts = 0;

    private int quorumNodes = 0;

    private int maxWaitForQuorumAttempts = 5;


    public boolean isValidSegment() throws IgSegmentationException {
        return true;
    }

    public ClusterQuorumResolver setQuorumNodes(int nodes) throws IgSegmentationException {
        this.quorumNodes = nodes;
        return this;
    }

    public ClusterQuorumResolver setMaxWaitForQuorumAttempts(int attempts) throws IgSegmentationException {
        this.maxWaitForQuorumAttempts = attempts;
        return this;
    }

    public boolean isQuorum(GridKernalContext ctx) throws IgSegmentationException {
        if (ctx == null)
            return true;

        IgniteClusterEx cluster = ctx.grid().cluster();
        if (cluster == null)
            return true;

        IgniteLogger log = ctx.log(getClass());

        var serverNodes = 0;
        try {
            for (ClusterNode node : cluster.nodes()) {
                if (!node.isClient()) serverNodes += 1;
            }
            if (serverNodes > maxServerNodesSeen) maxServerNodesSeen = serverNodes;

            log.info("quorumNodes=" + quorumNodes + " serverNodes=" +  serverNodes + " maxServerNodesSeen=" + maxServerNodesSeen + " attempts=" + quorumAttempts + " maxWaitForQuorumAttempts=" + maxWaitForQuorumAttempts);
            if (serverNodes < quorumNodes) {
                if (quorumAttempts > maxWaitForQuorumAttempts) {
                    return false;
                }
                quorumAttempts += 1;
            }
        } catch (NullPointerException e) {
            log.warning("null", e);
        }
        return true;
    }
}
