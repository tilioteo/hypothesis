/**
 * 
 */
package org.vaadin.maps.shared.ui.gridlayout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.maps.shared.ui.AbstractLayoutState;

import com.vaadin.shared.Connector;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class GridLayoutState extends AbstractLayoutState {
    {
        primaryStyleName = "v-gridlayout";
    }
    public int rows = 0;
    public int columns = 0;
    public Map<Connector, ChildComponentData> childData = new HashMap<Connector, GridLayoutState.ChildComponentData>();

    public static class ChildComponentData implements Serializable {
        public int column1;
        public int row1;
        public int column2;
        public int row2;
    }
}