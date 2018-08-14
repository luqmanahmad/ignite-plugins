package com.ig.segmentation.network.exception;

import org.apache.ignite.IgniteCheckedException;

/**
 * IgSegmentation exception
 */
public class IgSegmentationException extends IgniteCheckedException {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * @param msg Error message.
     */
    public IgSegmentationException(String msg) {
        super(msg);
    }

    /**
     * @param msg Error message.
     * @param cause Optional nested exception (can be <tt>null</tt>).
     */
    public IgSegmentationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
