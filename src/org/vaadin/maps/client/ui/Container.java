/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public interface Container {
	
	public boolean contains(Widget widget);
	public Widget getWidget(int index);
	public int getWidgetCount();
	public int getWidgetIndex(Widget widget);
	public Iterator<Widget> iterator();
	public boolean remove(int index);
	public boolean remove(Widget widget);

}
