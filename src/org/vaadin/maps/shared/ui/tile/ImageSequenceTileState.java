/**
 * 
 */
package org.vaadin.maps.shared.ui.tile;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.maps.shared.ui.AbstractTileState;

import com.vaadin.shared.communication.URLReference;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ImageSequenceTileState extends AbstractTileState {
	{
		primaryStyleName = "v-imagesequencetile";
	}

	public List<URLReference> sources = new ArrayList<URLReference>();
	public List<String> sourceTypes = new ArrayList<String>();
	
	public int index = 0;

}
