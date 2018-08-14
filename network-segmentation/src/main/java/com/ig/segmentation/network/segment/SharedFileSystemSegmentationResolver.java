package com.ig.segmentation.network.segment;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.ignite.internal.util.tostring.GridToStringExclude;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;

import com.ig.segmentation.network.exception.IgSegmentationException;
import com.ig.segmentation.plugin.segmentation.IgSegmentationResolver;

/**
 * SharedFileSystemSegmentationResolver which will check whether a node is able to read and write
 * to a shared folder and validate the segment accordingly.
 */
public class SharedFileSystemSegmentationResolver implements IgSegmentationResolver
{

    private File sharedFolder;
    private String folderPath;

    @GridToStringExclude
    private URL folderPathUrl;

    private static final String FOLDER_PATH_DEC_MSG = "folderPath [Use setFolderPath() method to set the property]";
    private static final String FOLDER_PATH_ERROR_MSG = "Unable to resolve folder path. [%s]";

    /**
     * @return true if the segment is valid else false
     * @throws IgSegmentationException if an error occurred
     */
    public boolean isValidSegment() throws IgSegmentationException {
        if (sharedFolder == null) {
            sharedFolder = new File(getFolderPathURI());
        }

        return sharedFolder.canRead() && sharedFolder.canWrite();
    }

    /**
     * @param folderPath Folder path for read and write checks. This folder
     *                   needs to be shared across the network.
     *
     * @return {@code this} for chaining.
     */
    public SharedFileSystemSegmentationResolver setFolderPath(String folderPath) {
        this.folderPath = folderPath;

        return this;
    }

    /**
     * @return URL for shared folder through {@link org.apache.ignite.internal.util.IgniteUtils#resolveIgniteUrl(String)}
     *
     * @throws NullPointerException
     * @throws IgSegmentationException If unable to resolve {@link java.net.URL} from folderPath
     */
    private URI getFolderPathURI() throws IgSegmentationException {
        if (this.folderPath == null)
            A.notNull(folderPath, FOLDER_PATH_DEC_MSG);

        folderPathUrl = U.resolveIgniteUrl(this.folderPath);

        if (folderPathUrl == null)
            throw new IgSegmentationException(String.format(FOLDER_PATH_ERROR_MSG, this.folderPath));

        try {
            return folderPathUrl.toURI();
        }
        catch (URISyntaxException e) {
            throw new IgSegmentationException(String.format(FOLDER_PATH_ERROR_MSG, e.getMessage()), e);
        }
    }

    public String toString() {
        return S.toString(SharedFileSystemSegmentationResolver.class, this);
    }
}