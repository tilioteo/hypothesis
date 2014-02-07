/**
 * 
 */
package org.vaadin.maps.server;

import java.net.URL;

import com.vaadin.server.ExternalResource;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class ImageResource extends ExternalResource implements TileResource {

    /**
     * Url of the image.
     */
    //private String imageURL = null;

    /**
     * MIME Type for the image
     */
    //private String mimeType = null;
    
    /**
     * Creates a new component for downloading directly from given URL.
     * 
     * @param imageURL
     *            the image URL.
     */
    public ImageResource(URL imageURL) {
    	super(imageURL);
        /*if (imageURL == null) {
            throw new RuntimeException("Source must be non-null");
        }

        this.imageURL = imageURL.toString();*/
    }

    /**
     * Creates a new component for downloading directly from given URL.
     * 
     * @param imageURL
     *            the image URL.
     */
    public ImageResource(String imageURL) {
    	super(imageURL);
        /*if (imageURL == null) {
            throw new RuntimeException("Source must be non-null");
        }

        this.imageURL = imageURL.toString();*/
    }

    /**
     * Gets the URL of the external resource.
     * 
     * @return the URL of the external resource.
     */
    /*public String getURL() {
        return imageURL;
    }*/

    /**
     * Gets the MIME type of the resource.
     * 
     * @see com.vaadin.server.Resource#getMIMEType()
     */
    /*@Override
    public String getMIMEType() {
        if (mimeType == null) {
            mimeType = FileTypeResolver.getMIMEType(getURL().toString());
        }
        return mimeType;
    }*/

}
