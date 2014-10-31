/**
 * 
 */
package org.vaadin.maps.ui.tile;

import java.net.URL;

import org.vaadin.maps.server.ImageResource;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class ImageTile extends AbstractProxyTile<ImageResource> {
	
	public ImageTile() {
		super();
	}
	
	public ImageTile(URL imageURL) {
		super(new ImageResource(imageURL));
	}

	public ImageTile(String imageURL) {
		super(new ImageResource(imageURL));
	}
	
	public void setImageUrl(URL imageURL) {
		setSource(new ImageResource(imageURL));
	}

	public void setImageUrl(String imageURL) {
		setSource(new ImageResource(imageURL));
	}

	@Override
	protected void clippedSizeChanged(int oldWidth, int oldHeight) {
		// TODO Auto-generated method stub
		
	}
}
