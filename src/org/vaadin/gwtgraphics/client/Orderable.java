/**
 * 
 */
package org.vaadin.gwtgraphics.client;

/**
 * @author morong
 * 
 */
public interface Orderable {

	AbstractDrawing insert(AbstractDrawing d, int beforeIndex);

	AbstractDrawing get(int index);

}
