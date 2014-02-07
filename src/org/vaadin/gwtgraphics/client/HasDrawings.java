/*
 * Copyright 2011 Henri Kerola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.gwtgraphics.client;

import java.util.Iterator;

/**
 * Classes implementing this class are able to contain VectorObjects.
 * 
 * renamed from VectorObjectContainer
 * 
 * @author Henri Kerola
 * 
 */
public interface HasDrawings {

	/**
	 * Adds a child drawing.
	 * 
	 * @param d
	 *            the drawing to be added
	 * @return added drawing
	 * @throws UnsupportedOperationException
	 *             if this method is not supported (most often this means that a
	 *             specific overload must be called)
	 */
	AbstractDrawing add(AbstractDrawing d);

	/**
	 * Insert the given VectorObject before the specified index.
	 * 
	 * If the VectorObjectContainer contains already the VectorObject, it will
	 * be removed from the VectorObjectContainer before insertion.
	 * 
	 * @param vo
	 *            VectorObject to be inserted
	 * @param beforeIndex
	 *            the index before which the VectorObject will be inserted.
	 * @return inserted VectorObject
	 * @throws IndexOutOfBoundsException
	 *             if <code>beforeIndex</code> is out of range
	 */
	//public abstract AbstractDrawing insert(AbstractDrawing vo, int beforeIndex);

	/**
	 * Gets an iterator for the contained drawings. This iterator is required to
	 * implement {@link Iterator#remove()}.
	 */
	Iterator<AbstractDrawing> iterator();

	/**
	 * Removes a child drawing.
	 * 
	 * @param d
	 *            the drawing to be removed
	 * 
	 * @return removed drawing or null if the container doesn't contain
	 *         the given drawing
	 */
	AbstractDrawing remove(AbstractDrawing d);

	/**
	 * 
	 * Brings the given VectorObject to front in this VectorObjectContainer.
	 * 
	 * @param vo
	 *            VectorObject to be brought to front
	 * @return the popped VectorObject
	 */
	//public abstract AbstractDrawing bringToFront(AbstractDrawing vo);

	/**
	 * Removes all child drawings.
	 */
	void clear();

	/**
	 * Returns the number of VectorObjects in this VectorObjectContainer.
	 * 
	 * @return the number of VectorObjects in this VectorObjectContainer.
	 */
	//public abstract int getVectorObjectCount();

	/**
	 * Returns the VectorObject element at the specified position.
	 * 
	 * @param index
	 *            index of element to return.
	 * @return the VectorObject element at the specified position.
	 */
	//public abstract AbstractDrawing getVectorObject(int index);

}